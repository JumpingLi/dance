package com.champion.dance.controller;

import com.champion.dance.domain.dto.CourseSubscribeDto;
import com.champion.dance.domain.entity.*;
import com.champion.dance.domain.enumeration.*;
import com.champion.dance.exception.BusinessException;
import com.champion.dance.response.ResultBean;
import com.champion.dance.service.*;
import com.champion.dance.utils.DateTimeUtil;
import com.champion.dance.utils.PaginationUtil;
import com.champion.dance.utils.page.PaginationList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created with dance
 * Author: jiangping.li
 * Date: 2018/2/26 15:20
 * Description：会员中心
 */
@Slf4j
@Validated
@RestController
//@CrossOrigin(origins = "*",maxAge = 3600)
public class MemberController {
    @Autowired
    private MemberService memberService;
    @Autowired
    private MemberCardService memberCardService;
    @Autowired
    private MemberCardBuyService memberCardBuyService;
    @Autowired
    private StudioService studioService;
    @Autowired
    private CourseSubscribeService courseSubscribeService;
    @Autowired
    private TeacherService teacherService;
    /**
     * 会员信息
     * @return
     */
    @RequestMapping(path = {"/member/info"},method = RequestMethod.GET)
    public ResultBean<?> memberInfo(HttpServletRequest request){
        String openId = (String) request.getAttribute("sessionId");
        Member member = memberService.findByOpenId(openId).orElse(null);
        if (member != null && !StringUtils.isEmpty(member.getRealName())) {
            Integer courseSubCount = courseSubscribeService.findAllByMemberId(member.getId());
            MemberResp resp = new MemberResp();
            BeanUtils.copyProperties(member,resp);
            resp.setCourseSubCount(courseSubCount);
            return new ResultBean<>(resp);
        }
        return new ResultBean<>("用户未注册会员",false);
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    private static class MemberResp extends Member{
        private Integer courseSubCount;
    }

    /**
     * 已约课程列表
     * @param request
     * @param page
     * @param limit
     * @return
     */
    @RequestMapping(path = {"/member/courses"},method = RequestMethod.GET)
    public Object findAllMemberCourse(HttpServletRequest request,
                                      @RequestParam(name = "page" , defaultValue = "1") Integer page,
                                      @RequestParam(name = "limit" , defaultValue = "10") Integer limit){
        String openId = (String) request.getAttribute("sessionId");
        Member member = memberService.findByOpenId(openId).orElseThrow(() -> new BusinessException("会员信息异常!"));
        List<CourseSubscribeDto> dtoList = courseSubscribeService.findCourseSubscribeByParams(ImmutableMap.<String,Object>builder()
                        .put("memberId",member.getId())
                        .build(),
                PaginationUtil.getRowBounds(page, limit));
        List<CourseSubscribeResp> respList = PaginationUtil.isPagination(page, limit) ? new PaginationList<>(dtoList) : Lists.newLinkedList();
        respList.addAll(dtoList.stream().map(courseSubscribeDto -> {
            CourseSubscribeResp resp = new CourseSubscribeResp();
            BeanUtils.copyProperties(courseSubscribeDto, resp);
            LocalTime beginTime = courseSubscribeDto.getBeginTime().toLocalDateTime().toLocalTime();
            if(courseSubscribeDto.getStatus() == CourseSubscribeStatus.SUBSCRIBED){
                if(courseSubscribeDto.getCourseDate().toLocalDate().isBefore(LocalDate.now())
                        || (courseSubscribeDto.getCourseDate().toLocalDate().compareTo(LocalDate.now()) ==0 && LocalTime.now().isAfter(beginTime))){
                    resp.setStatus(CourseSubscribeStatus.USED);
                }
            }
            Teacher teacher = teacherService.findById(courseSubscribeDto.getTeacherId());
            resp.setTeacherName(teacher.getName());
            LocalTime beginLocalTime = courseSubscribeDto.getBeginTime().toLocalDateTime().toLocalTime();
            LocalTime endLocalTime = courseSubscribeDto.getEndTime().toLocalDateTime().toLocalTime();
            String time = DateTimeUtil.formatLocalTime(beginLocalTime) + "-" + DateTimeUtil.formatLocalTime(endLocalTime);
            resp.setTime(time);
            return resp;
        }).collect(Collectors.toList()));
        return respList;
    }


    @Data
    @EqualsAndHashCode(callSuper = true)
    private static class CourseSubscribeResp extends CourseSubscribeDto{
        private String teacherName;
        private String time;
    }
    /**
     * 会员卡列表
     * @param request
     * @return
     */
    @RequestMapping(path = {"/member/cards"},method = RequestMethod.GET)
    public Object findAllMemberCard(HttpServletRequest request,
                                    @RequestParam(name = "page" , defaultValue = "1") Integer page,
                                    @RequestParam(name = "limit" , defaultValue = "10") Integer limit){
        String openId = (String) request.getAttribute("sessionId");
        Member member = memberService.findByOpenId(openId).orElseThrow(() -> new BusinessException("会员信息异常!"));
        List<MemberCard> memberCardList = memberCardService.findByMemberId(member.getId(), PaginationUtil.getRowBounds(page, limit));
        List<MemberCardResp> respList = PaginationUtil.isPagination(page, limit) ? new PaginationList<>(memberCardList) : Lists.newLinkedList();
        respList.addAll(memberCardList.stream().map(memberCard -> {
            MemberCardResp resp = new MemberCardResp();
            BeanUtils.copyProperties(memberCard, resp);
            if(memberCard.getExpirationTime().toLocalDateTime().isBefore(LocalDateTime.now())){
                resp.setStatus(CardStatus.DISABLED);//过期失效啦
            }
            Studio studio = studioService.findById(memberCard.getStudioId());
            resp.setStudioName(studio.getName());//工作室名称
            return resp;
        }).collect(Collectors.toList()));
        return respList;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    private static class MemberCardResp extends MemberCard{
        private String studioName;
    }
    /**
     * 购买会员卡信息列表
     * @param request
     * @return
     */
    @RequestMapping(path = {"/member/card/buy-info"},method = RequestMethod.GET)
    public Object findAllBuyMemberCardInfo(HttpServletRequest request,
                                           @RequestParam(name = "page" , defaultValue = "1") Integer page,
                                           @RequestParam(name = "limit" , defaultValue = "10") Integer limit){
        String openId = (String) request.getAttribute("sessionId");
        Member member = memberService.findByOpenId(openId).orElseThrow(() -> new BusinessException("会员信息异常!"));
        List<MemberCardBuyInfo> infoList = memberCardBuyService.findByMemberId(member.getId(), PaginationUtil.getRowBounds(page, limit));
        List<MemberCardBuyInfo> respList = PaginationUtil.isPagination(page, limit) ? new PaginationList<>(infoList) : Lists.newLinkedList();
        respList.addAll(infoList);
        return respList;
    }

    /**
     * 优惠码兑换7次体验卡
     * @param request
     * @param code
     * @return
     */
    @RequestMapping(path = {"/member/card/get/{code}"},method = RequestMethod.GET)
    public ResultBean<?> getMemberCardByCode(HttpServletRequest request,
                                             @PathVariable String code){
        String openId = (String) request.getAttribute("sessionId");
        Member member = memberService.findByOpenId(openId).orElseThrow(() -> new BusinessException("会员信息异常!"));
        MemberCard card = memberCardService.findByMemberId(member.getId())
                .stream()
                .filter(card1 -> card1.getNameType() == NameType.TIMES_7_TY)
                .findAny()
                .orElse(null);
        if(!Objects.isNull(card)){
            return new ResultBean<>("你已经有一张体验卡了,快去体验吧！",false);
        }
        MemberCard memberCard = memberCardService.findByCode(code);
        if(Objects.isNull(memberCard) || !StringUtils.isEmpty(memberCard.getMemberId())){
            return new ResultBean<>("获取体验卡失败,优惠码错误",false);
        }else{
            memberCardService.updateByCardId(MemberCard.builder()
                    .id(memberCard.getId())
                    .activationTime(Timestamp.valueOf(LocalDateTime.now()))
                    .expirationTime(Timestamp.valueOf(LocalDateTime.now().plusWeeks(1)))
                    .memberId(member.getId())
                    .build());
            return new ResultBean<>("获取体验卡成功",true);
        }
    }

    /**
     * 管理员录入 会员卡
     * @param req
     * @return
     */
    @RequestMapping(path = {"/member/card/record"},method = RequestMethod.POST)
    public ResultBean<?> recordMemberCard(@RequestBody @Valid  MemberCardReq req){
        Member member = memberService.findByMobile(req.getMobile());
        if(Objects.isNull(member)){
            return new ResultBean<>("系统查无此会员!",false);
        }
        CardType type = null;
        Integer remainCount = null;
        Timestamp expirationTime = null;
        String name = null;
        if(req.getNameType() == NameType.TIMES_1){
            name = "单次体验卡";
            remainCount = 1 + req.getGiveCount();
            type = CardType.TIMES;
            expirationTime = Timestamp.valueOf(LocalDateTime.now().plusMonths(1).plusDays(req.getGiveDays()));
            MemberCard memberCard = memberCardService.findByMemberId(member.getId()).stream().findAny().orElse(null);
            if(memberCard != null){
                return new ResultBean<>("该账号曾开通过" + memberCard.getName(),false);
            }
        }
        if(req.getNameType() == NameType.TIMES_50){
            name = "50次卡";
            remainCount = 50 + req.getGiveCount();
            type = CardType.TIMES;
            expirationTime = Timestamp.valueOf(LocalDateTime.now().plusYears(1).plusDays(req.getGiveDays()));
        }
        if(req.getNameType() == NameType.TIMES_12){
            name = "12次卡";
            remainCount = 12 + req.getGiveCount();
            type = CardType.TIMES;
            expirationTime = Timestamp.valueOf(LocalDateTime.now().plusDays(45 + req.getGiveDays()));
        }

        if(req.getNameType() == NameType.TIMES_30){
            name = "30次卡";
            remainCount = 30 + req.getGiveCount();
            type = CardType.TIMES;
            expirationTime = Timestamp.valueOf(LocalDateTime.now().plusMonths(6).plusDays(req.getGiveDays()));
        }
        if(req.getNameType() == NameType.TIMES_60){
            name = "60次卡";
            remainCount = 60 + req.getGiveCount();
            type = CardType.TIMES;
            expirationTime = Timestamp.valueOf(LocalDateTime.now().plusYears(1).plusDays(req.getGiveDays()));
        }
        if(req.getNameType() == NameType.TIMES_100){
            name = "100次卡";
            remainCount = 100 + req.getGiveCount();
            type = CardType.TIMES;
            expirationTime = Timestamp.valueOf(LocalDateTime.now().plusYears(1).plusDays(req.getGiveDays()));
        }
        if(req.getNameType() == NameType.PERIOD_7DAY_CT){
            name = "7天畅跳卡";
            remainCount = 100000;
            type = CardType.PERIOD;
            expirationTime = Timestamp.valueOf(LocalDateTime.now().plusDays(7).plusDays(req.getGiveDays()));
        }
        if(req.getNameType() == NameType.PERIOD_WEEKEND){
            name = "周末年卡";
            remainCount = 100000;
            type = CardType.PERIOD;
            expirationTime = Timestamp.valueOf(LocalDateTime.now().plusYears(1).plusDays(req.getGiveDays()));
        }
        if(req.getNameType() == NameType.PERIOD_QUARTER_YEAR){
            name = "季卡";
            remainCount = 100000;
            type = CardType.PERIOD;
            expirationTime = Timestamp.valueOf(LocalDateTime.now().plusMonths(3).plusDays(req.getGiveDays()));
        }
        if(req.getNameType() == NameType.PERIOD_HALF_YEAR){
            name = "半年卡";
            remainCount = 100000;
            type = CardType.PERIOD;
            expirationTime = Timestamp.valueOf(LocalDateTime.now().plusMonths(6).plusDays(req.getGiveDays()));
        }
        if(req.getNameType() == NameType.PERIOD_YEAR){
            name = "年卡";
            remainCount = 100000;
            type = CardType.PERIOD;
            expirationTime = Timestamp.valueOf(LocalDateTime.now().plusYears(1).plusDays(req.getGiveDays()));
        }
        if(req.getNameType() == NameType.DIAN_PING){
            name = "点评团购卡";
            remainCount = req.getCount() + req.getGiveCount();
            type = CardType.TIMES;
            expirationTime = Timestamp.valueOf(LocalDateTime.now().plusMonths(1).plusDays(req.getGiveDays()));
            MemberCard memberCard = memberCardService.findByMemberId(member.getId()).stream().findAny().orElse(null);
            if(memberCard != null){
                return new ResultBean<>("该账号曾开通过" + memberCard.getName(),false);
            }
        }
        String cardId =RandomStringUtils.randomAlphabetic(2).toUpperCase() +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("YYMMdd")) + RandomStringUtils.randomNumeric(5);
        memberCardBuyService.insertBuyInfo(MemberCardBuyInfo.builder()
                .memberId(member.getId())
                .content("购买" + name + ",充值" + req.getAmount() + "元(含赠送" + req.getGiveCount() + "次,赠送" + req.getGiveDays() + "天)")
                .operator(req.getOperator())
                .createTime(Timestamp.valueOf(LocalDateTime.now()))
                .build());
        memberCardService.insertMemberCard(MemberCard.builder()
                .id(cardId)
                .memberId(member.getId())
                .code(req.getCode())
                .name(name)
                .nameType(req.getNameType())
                .type(type)
                .activationTime(Timestamp.valueOf(LocalDateTime.now()))
                .expirationTime(expirationTime)
                .remainCount(remainCount)
                .amount(req.getAmount())
                .counselor(req.getCounselor())
                .operator(req.getOperator())
                .chargeType(req.getChargeType())
                .build());
        return new ResultBean<>("录入成功!",true);
    }

    @Data
    private static class MemberCardReq{
        private NameType nameType;//卡种
        @Pattern(regexp = "^1([358][0-9]|4[579]|66|7[0135678]|9[89])[0-9]{8}$",message = "手机号码格式不正确")
        private String mobile;
        private BigDecimal amount;
        private String code;
        private String counselor;//顾问
        private String operator;//操作人
        private ChargeType chargeType;//充值类型
        @Range(min = 0, message = "赠送次数不能为空")
        private Integer giveCount;//赠送次数
        @Range(min = 0, message = "赠送天数不能为空")
        private Integer giveDays;// 赠送天数
        @Range(min = 1, message = "团购次数不能为空")
        private Integer count;
    }
}
