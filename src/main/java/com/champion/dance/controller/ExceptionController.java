package com.champion.dance.controller;

import com.champion.dance.exception.BusinessException;
import com.champion.dance.response.ResultBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;

@Slf4j
@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Object handleHttpReadableException(HttpMessageNotReadableException e) {
        log.error("http message not readable exception: ", e);
        return new ResultBean<>(e.getMessage(),false);
    }


    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Object handleBusinessException(BusinessException e) {
        log.error("business exception: {}", e.getMessage());
        return new ResultBean<>(e.getMessage(),false);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public Object handleErrorException(Exception e) {
        log.error("sever error exception: ", e);
        return new ResultBean<>("系统业务高峰,请稍后再试!",false);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Object handleConstraintViolationException(ConstraintViolationException e){
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        StringBuilder stringBuilder = new StringBuilder();
        for (ConstraintViolation<?> item : violations) {
            stringBuilder = stringBuilder.append(item.getMessage());
        }
        log.error("request param error: {}", stringBuilder.toString());
        return new ResultBean<>(stringBuilder.toString(),false);
    }
}
