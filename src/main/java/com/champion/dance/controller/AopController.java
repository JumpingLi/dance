package com.champion.dance.controller;

import com.champion.dance.exception.BusinessException;
import com.champion.dance.response.ResultBean;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;

/**
 * Created with payment-service
 * User: jiangping.li
 * Date: 2017/9/12
 * Time: 10:28
 * description:AOP统一处理Controller接口,先于ExceptionHandler
 */
@Slf4j
@RestController
@Aspect
public class AopController {
    @Around(value = "execution(public com.champion.dance.response.ResultBean *(..))")
    private ResultBean<?> handlerControllerMethod(ProceedingJoinPoint pjp) {
        long startTime = System.currentTimeMillis();
        ResultBean<?> result;
        try {
            result = (ResultBean<?>) pjp.proceed();
            log.debug(pjp.getSignature() + " used time:" + (System.currentTimeMillis() - startTime));
        } catch (Throwable e) {
            result = handlerException(pjp, e);
        }
        return result;
    }

    private ResultBean<?> handlerException(ProceedingJoinPoint pjp, Throwable e) {
        ResultBean<?> result = new ResultBean(e);
        // 分类处理各种业务主动抛出的已知异常
        if (e instanceof BusinessException) {
            log.info(pjp.getSignature() + ",message:{}", e.getMessage());
        } else if (e instanceof ConstraintViolationException) {
            ConstraintViolationException cve = (ConstraintViolationException) e;
            Set<ConstraintViolation<?>> violations = cve.getConstraintViolations();
            StringBuilder stringBuilder = new StringBuilder();
            for (ConstraintViolation<?> item : violations) {
                stringBuilder = stringBuilder.append(item.getMessage());
            }
            log.info(pjp.getSignature() + ",message:{}", stringBuilder.toString());
            result.setMsg(stringBuilder.toString());
        } else {
            log.error(pjp.getSignature() + ",error:", e);
            result.setMsg("当前系统业务忙,请稍候再试!");
        }
        return result;
    }
}
