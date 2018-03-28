package com.champion.dance.controller;

import com.aliyuncs.exceptions.ClientException;
import com.champion.dance.domain.entity.Member;
import com.champion.dance.domain.enumeration.Sex;
import com.champion.dance.exception.BusinessException;
import com.champion.dance.message.SmsDayuInfo;
import com.champion.dance.response.ResultBean;
import com.champion.dance.service.MemberService;
import com.champion.dance.utils.AliDayuSms;
import com.champion.dance.utils.DateTimeUtil;
import com.champion.dance.utils.ParamValidator;
import com.champion.wechat.entity.AccessTokenOAuth;
import com.champion.wechat.entity.user.UserWeiXin;
import com.champion.wechat.service.OAuthService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created with dance
 * Author: jiangping.li
 * Date: 2018/2/25 13:21
 * Description：
 */
@Slf4j
@Validated
@RestController
//@CrossOrigin(origins = "*",maxAge = 3600)
public class LoginController {
    @Autowired
    private MemberService memberService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @RequestMapping(path = {"/index"},method = RequestMethod.GET)
    public ResultBean<?> index(){
        return new ResultBean<>("This service provide by champion!");
    }

    @RequestMapping(path = {"/wx/code"},method = RequestMethod.GET)
    public void getWechatCode(HttpServletResponse response,
                              @RequestParam String code) throws IOException {
//        Boolean hasKey = stringRedisTemplate.hasKey(code);
//        if(hasKey){
//            response.sendRedirect("http://www.vecdole.com/app/html/interim.html?pageCode=1");
//            return;
//        }
//        stringRedisTemplate.opsForValue().set(code,"wechat code",5,TimeUnit.SECONDS);
        log.info("------ code:" + code);
        AccessTokenOAuth accessTokenOAuth = OAuthService.getOAuthAccessToken(code);
//        log.info("------ accessToken:" + accessTokenOAuth.getAccessToken());
        log.info("------ openId:" + accessTokenOAuth.getOpenid());
        String openId = accessTokenOAuth.getOpenid();
        Member member = memberService.findByOpenId(openId).orElse(null);
        if(Objects.isNull(member)){
            UserWeiXin userWeiXin = OAuthService.getUserInfoOauth(accessTokenOAuth.getAccessToken(),accessTokenOAuth.getOpenid());
            log.info("------ headimgurl:" + userWeiXin.getHeadimgurl());
            memberService.insertMember(Member.builder()
                    .id(UUID.randomUUID().toString())
                    .openId(openId)
                    .avatarUrl(userWeiXin.getHeadimgurl())
                    .build());
            response.sendRedirect("http://www.vecdole.com/app/html/interim.html?openId=1_Uni5_" + openId);
        }else if(StringUtils.isEmpty(member.getRealName())){
            response.sendRedirect("http://www.vecdole.com/app/html/interim.html?openId=1_Uni5_" + openId);
        }else{
            // 注册用户,授权成功
            response.sendRedirect("http://www.vecdole.com/app/html/interim.html?openId=2_Uni5_" + openId);
        }
    }

    @RequestMapping(path = {"/wx/auth"},method = RequestMethod.GET)
    public void wxAuth(HttpServletResponse response) throws IOException {
        String authUrl = OAuthService.getOauthUrl("http://www.vecdole.com/api/wx/code","UTF-8","snsapi_userinfo");
        response.sendRedirect(authUrl);
    }

    @RequestMapping(path = {"/wx"},method = RequestMethod.GET)
    public void wx(HttpServletRequest request,
                   HttpServletResponse response) throws IOException {
        String echostr = request.getParameter("echostr");
        PrintWriter out = response.getWriter();
        out.print(echostr);
    }

    @RequestMapping(path = {"/member/auth-code/{mobile}"},method = RequestMethod.GET)
    public ResultBean<?> sendAuthCode(
            @Pattern(regexp = "^1([358][0-9]|4[579]|66|7[0135678]|9[89])[0-9]{8}$",message = "手机号码格式不正确")
            @PathVariable String mobile){
        String redisValue = stringRedisTemplate.opsForValue().get(mobile);
        if(!StringUtils.isEmpty(redisValue)){
            return new ResultBean<>("操作太频繁,请稍后再试!",true);
        }
        String authCode = RandomStringUtils.randomNumeric(6);
        stringRedisTemplate.opsForValue().set(mobile,authCode,60*5, TimeUnit.SECONDS);
        //todo 阿里通信发送短信验证码
        try {
            AliDayuSms.sendSms(SmsDayuInfo.builder()
                    .phoneNumber(mobile)
                    .signName("怡霓信息")
                    .templateCode("SMS_126640360")
                    .templateParam("{\"code\":\"" + authCode + "\"}")//模板参数json串
                    .build());
        } catch (ClientException e) {
            log.error("阿里通信短信服务异常:",e);
            throw new BusinessException("短信服务异常,请稍候再试!");
        }
        return new ResultBean<>("短信验证码已发送!",true);
    }

    @RequestMapping(path = {"/member/register"},method = RequestMethod.POST)
    public ResultBean<?> memberRegister(@RequestBody @Valid RegisterReq req, BindingResult result){
        ParamValidator.paramValidate(result);
        Member memberMobile = memberService.findByMobile(req.getMobile());
        if(!Objects.isNull(memberMobile)){
            return new ResultBean<>("手机号已注册!",false);
        }
        String authCode = stringRedisTemplate.opsForValue().get(req.getMobile());
        if(!req.getAuthCode().equals(authCode)){
            throw new BusinessException("短信验证码验证错误!");
        }
        Member member = memberService.findByOpenId(req.getOpenId()).orElse(null);
        Member newOrUpdateMember = Member.builder()
                .nickname(req.getNickname())
                .realName(req.getRealName())
                .mobile(req.getMobile())
                .sex(req.getSex())
                .age(req.getAge())
                .birthday(Date.valueOf(DateTimeUtil.parseDate(req.getBirthday())))
                .danceAge(req.getDanceAge())
                .danceId(req.getDanceId())
                .province(req.getProvince())
                .city(req.getCity())
                .district(req.getDistrict())
                .build();
        if(member == null){
            // 授权会写入member,注册无此种情况
            newOrUpdateMember.setId(UUID.randomUUID().toString());
            newOrUpdateMember.setOpenId(req.getOpenId());
            memberService.insertMember(newOrUpdateMember);
        }else{
            newOrUpdateMember.setId(member.getId());
            memberService.updateMemberById(newOrUpdateMember);
        }
        return new ResultBean<>("注册成功",true);
    }

    @Data
    private static class RegisterReq{
        @NotBlank(message = "openId不能为空")
        private String openId;
        @NotBlank(message = "短信验证码不能为空")
        private String authCode;
        @NotBlank(message = "舞种不能为空")
        private String danceId;
        @NotBlank(message = "用户昵称不能为空")
        private String nickname;
        @NotBlank(message = "真实姓名不能为空")
        private String realName;
        @Pattern(regexp = "^1([358][0-9]|4[579]|66|7[0135678]|9[89])[0-9]{8}$",message = "手机号码格式不正确")
        private String mobile;
        @Range(min = 5, max = 100, message = "年龄不能为空")
        private Integer age;
        @Pattern(regexp = "^[0-9]{4}-[0-9]{2}-[0-9]{2}$",message = "课程时间格式错误")
        private String birthday;
        @NotBlank(message = "省区不能为空")
        private String province;
        @NotBlank(message = "城市不能为空")
        private String city;
        @NotBlank(message = "区县不能为空")
        private String district;
        @Range(min = 0, max = 100, message = "舞蹈年龄不能为空")
        private Integer danceAge;
        private Sex sex;
    }
}
