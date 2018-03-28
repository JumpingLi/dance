package com.champion.dance.controller;

import com.champion.dance.domain.entity.Comment;
import com.champion.dance.domain.entity.CourseSubscriptInfo;
import com.champion.dance.domain.entity.Member;
import com.champion.dance.domain.enumeration.CourseSubscribeStatus;
import com.champion.dance.domain.enumeration.StartLevel;
import com.champion.dance.exception.BusinessException;
import com.champion.dance.response.ResultBean;
import com.champion.dance.service.CommentService;
import com.champion.dance.service.CourseSubscribeService;
import com.champion.dance.service.MemberService;
import com.champion.dance.utils.ParamValidator;
import com.google.common.collect.ImmutableMap;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Created with dance
 * Author: jiangping.li
 * Date: 2018/2/28 14:54
 * Description：
 */
@RestController
//@CrossOrigin(origins = "*",maxAge = 3600)
public class CommentController {

    @Autowired
    private CommentService commentService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private CourseSubscribeService courseSubscribeService;

    /**
     * 提交评价
     * @param request
     * @param req
     * @param result
     * @return
     */
    @RequestMapping(path = {"/comment/submit"},method = RequestMethod.POST)
    public ResultBean<?> submitComment(HttpServletRequest request,
                                       @RequestBody @Valid CommentReq req, BindingResult result){
        ParamValidator.paramValidate(result);
        String openId = (String) request.getAttribute("sessionId");
        Member member = memberService.findByOpenId(openId).orElseThrow(() -> new BusinessException("会员信息异常!"));
//        Comment comment = commentService.findByParams(ImmutableMap.<String,Object>builder()
//                .put("memberId",member.getId())
//                .put("courseId",req.getCourseId())
//                .build());
        commentService.insertComment(Comment.builder()
                .memberId(member.getId())
                .courseId(req.getCourseId())
                .startLevel(req.getStartLevel())
                .content(req.getContent())
                .build());
        courseSubscribeService.updateCourseSubscribe(CourseSubscriptInfo.builder()
                .id(req.getSubscribeId())
                .status(CourseSubscribeStatus.COMMENTED)//已评价
                .build());
        return new ResultBean<>("评价成功!",true);
    }

    @Data
    private static class CommentReq{
        @NotBlank(message = "评价内容不能为空")
        private String content;
        private StartLevel startLevel;
        @NotBlank(message = "课程Id不能为空")
        private String courseId;
        @Pattern(regexp = "^[0-9]{4}-[0-9]{2}-[0-9]{2}$",message = "课程时间格式错误")
        private String courseDate;

        private Integer subscribeId;
    }
}
