package com.champion.dance.controller;

import com.champion.dance.domain.entity.*;
import com.champion.dance.domain.enumeration.CardType;
import com.champion.dance.domain.enumeration.CourseSubscribeStatus;
import com.champion.dance.domain.enumeration.StartLevel;
import com.champion.dance.exception.BusinessException;
import com.champion.dance.response.ResultBean;
import com.champion.dance.service.*;
import com.champion.dance.utils.DateTimeUtil;
import com.champion.dance.utils.PaginationUtil;
import com.champion.dance.utils.page.PaginationList;
import com.champion.dance.domain.dto.CommentDto;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.*;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created with dance
 * Author: jiangping.li
 * Date: 2018/2/26 16:20
 * Description：
 */
@Slf4j
@RestController
@CrossOrigin(origins = "*",maxAge = 3600)
public class CourseController {

    @Autowired
    private CommentService commentService;
    @Autowired
    private CourseService courseService;
    @Autowired
    private TeacherService teacherService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private MemberCardService memberCardService;
    @Autowired
    private CourseSubscribeService courseSubscribeService;
    @Autowired
    private MemberService memberService;
    /**
     * 课程列表
     * @param request
     * @param subbranch
     * @param localDate yyyy-MM-dd
     * @return
     */
    @RequestMapping(path = {"/courses"},method = RequestMethod.GET)
    public Object findAllCourse(HttpServletRequest request,
                                @RequestParam(required = false) String subbranch,
                                @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate localDate){
        String openId = (String) request.getAttribute("sessionId");
        Member member = memberService.findByOpenId(openId).orElseThrow(() -> new BusinessException("会员信息异常!"));
        //todo 查询一周后的或者过去时间的课程列表返回空集合
        List<CourseVo> courseVoList = Lists.newLinkedList();
        if(localDate.isAfter(LocalDate.now().plusDays(6)) || localDate.isBefore(LocalDate.now())){
            return courseVoList;
        }
        List<Course> courseList = courseService.findAllCourse(localDate);
        List<String> teacherIds = courseList.stream().map(Course::getTeacherId).distinct().collect(Collectors.toList());
        Map<String,Teacher> teacherMap = teacherService.findAllTeacher(teacherIds);
        courseList.forEach(course -> {
            CourseVo courseVo = new CourseVo();
            BeanUtils.copyProperties(course,courseVo);
            courseVo.setTeacherName(teacherMap.get(course.getTeacherId()).getName());
            courseVo.setAvatarUrl(teacherMap.get(course.getTeacherId()).getAvatarUrl());
            LocalTime beginLocalTime = course.getBeginTime().toLocalDateTime().toLocalTime();
            LocalTime endLocalTime = course.getEndTime().toLocalDateTime().toLocalTime();
            String time = DateTimeUtil.formatLocalTime(beginLocalTime) + "-" + DateTimeUtil.formatLocalTime(endLocalTime);
            courseVo.setTime(time);
            if(localDate.isAfter(LocalDate.now().plusDays(1))){
                courseVo.setRemark("课前一天才能预约哦!");
                courseVo.setStatus(CourseStatus.DISABLED);//置灰-不可预约
            }else{
                //todo 约课状态
                LocalTime nowLocalTime = LocalTime.now();
                // todo 当前用户预约信息
                CourseSubscriptInfo courseSubscriptInfo = courseSubscribeService.findByParams(ImmutableMap.<String,Object>builder()
                        .put("memberId",member.getId())
                        .put("courseId",course.getId())
                        .put("courseDate",Date.valueOf(localDate))
                        .build())
                        .stream()
                        .filter(info -> info.getStatus() == CourseSubscribeStatus.SUBSCRIBED)
                        .findFirst()
                        .orElse(null);
                if(beginLocalTime.isBefore(nowLocalTime) && endLocalTime.isAfter(nowLocalTime)){
                    if(localDate.compareTo(LocalDate.now()) == 0){
                        courseVo.setStatus(CourseStatus.RUNNING);//已上课中
                    }else{
                        courseVo.setStatus(CourseStatus.UN_SUBSCRIBE);//可预约
                    }
                }else if(beginLocalTime.isAfter(nowLocalTime)){
                    if(courseSubscriptInfo != null){
                        courseVo.setStatus(CourseStatus.SUBSCRIBED);
                        courseVo.setSubscribeId(courseSubscriptInfo.getId());//预约id
                    }else{
                        if(course.getAliveAmount() == 0){
                            courseVo.setStatus(CourseStatus.IMPLETION);//约满
                        }else{
                            if(nowLocalTime.plusMinutes(10).isAfter(beginLocalTime)){
                                courseVo.setStatus(CourseStatus.UPCOMING);//即将上课
                            }else{
                                courseVo.setStatus(CourseStatus.UN_SUBSCRIBE);//可预约
                            }
                        }
                    }
                }else{
                    if(localDate.compareTo(LocalDate.now()) == 0){
                        courseVo.setStatus(CourseStatus.EXPIRE);//已过期
                    }else{
                        courseVo.setStatus(CourseStatus.UN_SUBSCRIBE);//可预约
                    }
                }
            }
            courseVoList.add(courseVo);
        });
        return courseVoList;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    private static class CourseVo extends Course{
        private String teacherName;
        private String avatarUrl;
        private CourseStatus status;
        private String remark;
        private Integer subscribeId;
        private String time;
    }

    private enum CourseStatus{
        DISABLED,//预约时间未到不可预约-置灰不给按钮事件
        UN_SUBSCRIBE,//放可预约按钮
        SUBSCRIBED,//已预约-放取消预约按钮
        IMPLETION,//已约满-不给按钮事件
        EXPIRE,//已结束-不给按钮事件
        RUNNING,//上课中-不给按钮事件
        UPCOMING,//即将上课-不给按钮事件
    }

    /**
     * 课程详情
     * @param request
     * @param courseId
     * @return
     */
    @RequestMapping(path = {"/course/detail/{courseId}/{courseDate}"},method = RequestMethod.GET)
    public ResultBean<?> getCourseDetail(HttpServletRequest request,
                                         @PathVariable String courseId,
                                         @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate courseDate){
        String openId = (String) request.getAttribute("sessionId");
        Member member = memberService.findByOpenId(openId).orElseThrow(() -> new BusinessException("会员信息异常!"));
        Course course = courseService.findByCourseId(courseId);
        if(course == null){
            return new ResultBean<>("课程数据异常!",false);
        }
        Teacher teacher = teacherService.findById(course.getTeacherId());
        if(teacher == null){
            return new ResultBean<>("教师数据异常!",false);
        }
        MemberCard memberCard = memberCardService.findByMemberId(member.getId())
                .stream()
                .filter(card -> card.getStudioId().equals(course.getStudioId()))//过滤-选择出可用的会员卡
                .findFirst()
                .orElse(null);
        CourseSubscriptInfo courseSubscriptInfo = courseSubscribeService.findByParams(ImmutableMap.<String,Object>builder()
                .put("memberId",member.getId())
                .put("courseId",course.getId())
                .put("courseDate",Date.valueOf(courseDate))
                .build())
                .stream()
                .filter(info -> info.getStatus() == CourseSubscribeStatus.SUBSCRIBED)
                .findFirst().orElse(null);
        LocalTime beginTime = course.getBeginTime().toLocalDateTime().toLocalTime();
        LocalTime endTime = course.getEndTime().toLocalDateTime().toLocalTime();
        int minutes = (int) DateTimeUtil.dateDifferenceMinutes(beginTime,endTime);
        String time = DateTimeUtil.formatLocalTime(beginTime) + "-" + DateTimeUtil.formatLocalTime(endTime);
        CourseDetail courseDetail = new CourseDetail();
        courseDetail.setMinutes(minutes);
        courseDetail.setCourseDate(DateTimeUtil.formatLocalDate(courseDate));
        courseDetail.setTime(time);
        courseDetail.setName(course.getDanceName());
        courseDetail.setStudioName(course.getStudioName());
        courseDetail.setDayOfWeek(course.getWeekDate());
        courseDetail.setComplexity(course.getComplexity());
        courseDetail.setTeacherName(teacher.getName());
        courseDetail.setAvatarUrl(teacher.getAvatarUrl());
        courseDetail.setIntro(teacher.getIntro());
        if(courseSubscriptInfo != null){
            courseDetail.setIsSubscribe(true);
            courseDetail.setSubscribeId(courseSubscriptInfo.getId());
        }else{
            courseDetail.setIsSubscribe(false);
        }
        if(memberCard == null){
            courseDetail.setIsHaveCard(false);
        }else{
            courseDetail.setIsHaveCard(true);
        }
        return new ResultBean<>(courseDetail);

    }
    @Data
    private static class CourseDetail{
        private String name;
        private String time;
        private String courseDate;
        private Integer minutes;
        private DayOfWeek dayOfWeek;
        private String studioName;
        private StartLevel complexity;

        private String teacherName;
        private String avatarUrl;
        private String intro;
        private Boolean isSubscribe;
        private Integer subscribeId;
        private Boolean isHaveCard;
    }

    @RequestMapping(path = {"/course/comments/{courseId}"},method = RequestMethod.GET)
    public Object findCommentByCourseId(@PathVariable String courseId,
                                        @RequestParam(name = "page" , defaultValue = "1") Integer page,
                                        @RequestParam(name = "limit" , defaultValue = "10") Integer limit){
        List<CommentDto> commentDtoList = commentService.findCommentsByCourseId(courseId, PaginationUtil.getRowBounds(page, limit));
        List<CommentDto> respList = PaginationUtil.isPagination(page, limit) ? new PaginationList<>(commentDtoList) : Lists.newLinkedList();
        respList.addAll(commentDtoList);
        return respList;

    }
    /**
     * 会员约课
     * @param request
     * @param courseId
     * @param subDate
     * @return
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(path = {"/course/subscribe"},method = RequestMethod.GET)
    public ResultBean<?> subscribeCourse(HttpServletRequest request,
                                        @RequestParam String courseId,
                                        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate subDate){
        String openId = (String) request.getAttribute("sessionId");
        Member member = memberService.findByOpenId(openId).orElseThrow(() -> new BusinessException("会员信息异常!"));
        if(subDate.isBefore(LocalDate.now()) || subDate.isAfter(LocalDate.now().plusDays(1))){
            throw new BusinessException("该日期不可预约哦!");
        }
        Course course = (Course) redisTemplate.opsForValue().get(courseId);
        if(!Objects.isNull(course)) {
            //todo 判断预约课程时间是否冲突
            LocalTime beginTime = course.getBeginTime().toLocalDateTime().toLocalTime();
            LocalTime endTime = course.getEndTime().toLocalDateTime().toLocalTime();
            List<CourseSubscriptInfo> infoList = courseSubscribeService.findByParams(ImmutableMap.<String,Object>builder()
                    .put("memberId",member.getId())
                    .put("courseDate", subDate)
                    .build())
                    .stream()
                    .filter(info -> {
                        Course subCourse = courseService.findByCourseId(info.getCourseId());
                        LocalTime subBeginTime = subCourse.getBeginTime().toLocalDateTime().toLocalTime();
//                        LocalTime subEndTime = subCourse.getEndTime().toLocalDateTime().toLocalTime();
                        if(info.getStatus() != CourseSubscribeStatus.CANCEL_SUBSCRIBE
                                && (subBeginTime.compareTo(beginTime) == 0 || (subBeginTime.isAfter(beginTime) && subBeginTime.isBefore(endTime)))){
                            return true;
                        }else{
                            return false;
                        }
                    }).collect(Collectors.toList());
            if(!CollectionUtils.isEmpty(infoList)){
                return new ResultBean<>("你已有预约课程,请预约其他时间段的课程吧!",false);
            }
            if(course.getAliveAmount() == 0){
                return new ResultBean<>("课程已约满,下次请早点来哦!",false);
            }
            MemberCard memberCard = memberCardService.findByMemberId(member.getId())
                    .stream()
                    .filter(card -> card.getStudioId().equals(course.getStudioId()))//过滤-选择出可用的会员卡
                    .findFirst()
                    .orElseThrow(() -> new BusinessException("您暂无该课程可用的会员卡!"));
            if(memberCard.getExpirationTime().toLocalDateTime().isBefore(LocalDateTime.now())){
                return new ResultBean<>("您的会员卡已过期失效!",false);
            }
            if(memberCard.getType() == CardType.TIMES && memberCard.getRemainCount() < 1){
                return new ResultBean<>("您的会员卡可用次数不足!",false);
            }
            String msg;
            if(memberCard.getType() == CardType.TIMES){
                msg = "卡内剩余次数:" + (memberCard.getRemainCount() - 1);
            }else{
                msg = memberCard.getName() + ",有效期内不限次数";
            }
            long seconds = 0;
            //预约当天的
            if(subDate.compareTo(LocalDate.now()) == 0){

                if(LocalTime.now().isAfter(beginTime) && LocalTime.now().isBefore(endTime)){
                    return new ResultBean<>("上课中,不能预约了哦!",false);
                }else if(LocalTime.now().isAfter(endTime)){
                    return new ResultBean<>("课程已结束啦!",false);
                }else if(LocalTime.now().plusMinutes(10).isAfter(beginTime)){
                    return new ResultBean<>("开课前10分钟,不能预约了哦!",false);
                }
                seconds = DateTimeUtil.dateDifferenceSeconds(LocalDateTime.now(),
                        LocalDateTime.of(subDate.plusDays(1).getYear(),subDate.plusDays(1).getMonth(),subDate.plusDays(1).getDayOfMonth(),0,0));
            }
            //预约第二天的
            if(subDate.compareTo(LocalDate.now().plusDays(1)) == 0){
                seconds = DateTimeUtil.dateDifferenceSeconds(LocalDateTime.now(),
                        LocalDateTime.of(subDate.plusDays(1).getYear(),subDate.plusDays(1).getMonth(),subDate.plusDays(1).getDayOfMonth(),0,0));
            }
            int aliveAmount = course.getAliveAmount() - 1;
            course.setAliveAmount(aliveAmount);
            redisTemplate.opsForValue().set(courseId,course,seconds, TimeUnit.SECONDS);
            courseSubscribeService.insertCourseSubscribe(CourseSubscriptInfo.builder()
                    .courseId(courseId)
                    .memberId(member.getId())
                    .courseDate(Date.valueOf(subDate))//只能预约当天和第二天的课程
                    .status(CourseSubscribeStatus.SUBSCRIBED)
                    .subscribeTime(Timestamp.valueOf(LocalDateTime.now()))
                    .build());
            memberCardService.updateByCardId(MemberCard.builder()
                    .id(memberCard.getId())
                    .remainCount(memberCard.getRemainCount() - 1)
                    .build());
            //todo 如需要，就短信推送一下吧
            return new ResultBean<>(msg);
        }else{
            return new ResultBean<>("课程数据异常!",false);
        }
    }


    /**
     * 取消约课
     * @param request
     * @param subscribeId
     * @return
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(path = {"/course/cancel"},method = RequestMethod.GET)
    public ResultBean<?> cancelCourse(HttpServletRequest request,
                                        @RequestParam(name = "subscribeId") Integer subscribeId){
        String openId = (String) request.getAttribute("sessionId");
        Member member = memberService.findByOpenId(openId).orElseThrow(() -> new BusinessException("会员信息异常!"));
        CourseSubscriptInfo courseSubscriptInfo = courseSubscribeService.findById(subscribeId);
        if(Objects.isNull(courseSubscriptInfo) || courseSubscriptInfo.getStatus() == CourseSubscribeStatus.CANCEL_SUBSCRIBE){
            return new ResultBean<>("预约数据异常!",false);
        }
        LocalDate courseDate = courseSubscriptInfo.getCourseDate().toLocalDate();
        if(courseDate.isBefore(LocalDate.now()) || courseDate.isAfter(LocalDate.now().plusDays(1))){
            throw new BusinessException("预约信息已失效,无需取消!");
        }
        Course course = (Course) redisTemplate.opsForValue().get(courseSubscriptInfo.getCourseId());
        if(!Objects.isNull(course)) {
            MemberCard memberCard = memberCardService.findByMemberId(member.getId())
                    .stream()
                    .filter(card -> card.getStudioId().equals(course.getStudioId()))//过滤-选择出可用的会员卡
                    .findFirst()
                    .orElseThrow(() -> new BusinessException("您暂无该课程可用的会员卡!"));
            if(memberCard.getExpirationTime().toLocalDateTime().isBefore(LocalDateTime.now())){
                return new ResultBean<>("您的会员卡已过期失效!",false);
            }
            long seconds = 0;
            //取消当天的预约
            if(courseDate.compareTo(LocalDate.now()) == 0){
                LocalTime beginTime = course.getBeginTime().toLocalDateTime().toLocalTime();
                if(LocalTime.now().isAfter(beginTime) || LocalTime.now().plusMinutes(10).isAfter(beginTime)){
                    return new ResultBean<>("课程即将开始或已开始,不能取消了哦!",false);
                }
                seconds = DateTimeUtil.dateDifferenceSeconds(LocalDateTime.now(),
                        LocalDateTime.of(courseDate.plusDays(1).getYear(),courseDate.plusDays(1).getMonth(),courseDate.plusDays(1).getDayOfMonth(),0,0));
            }
            //取消第二天的预约
            if(courseDate.compareTo(LocalDate.now().plusDays(1)) == 0){
                seconds = DateTimeUtil.dateDifferenceSeconds(LocalDateTime.now(),
                        LocalDateTime.of(courseDate.plusDays(1).getYear(),courseDate.plusDays(1).getMonth(),courseDate.plusDays(1).getDayOfMonth(),0,0));
            }
            int aliveAmount = course.getAliveAmount() + 1;
            course.setAliveAmount(aliveAmount);
            redisTemplate.opsForValue().set(courseSubscriptInfo.getCourseId(),course,seconds, TimeUnit.SECONDS);
            courseSubscribeService.updateCourseSubscribe(CourseSubscriptInfo.builder()
                    .id(subscribeId)
                    .status(CourseSubscribeStatus.CANCEL_SUBSCRIBE)
                    .build());
            memberCardService.updateByCardId(MemberCard.builder()
                    .id(memberCard.getId())
                    .remainCount(memberCard.getRemainCount() + 1)
                    .build());
            //todo 如需要，就短信推送一下吧
            return new ResultBean<>("取消成功!",true);
        }else{
            return new ResultBean<>("课程数据异常!",false);
        }
    }
}
