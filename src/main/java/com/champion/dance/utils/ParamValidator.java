package com.champion.dance.utils;

import com.champion.dance.exception.BusinessException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

/**
 * Created with dance
 * Author: jiangping.li
 * Date: 2018/2/28 15:11
 * Description：
 */
public class ParamValidator {
    public static void paramValidate(BindingResult result){
        if(result.hasErrors()){
            StringBuilder stringBuilder = new StringBuilder();
            for (ObjectError error : result.getAllErrors()) {
                stringBuilder = stringBuilder.append(error.getDefaultMessage());
            }
            throw new BusinessException(stringBuilder.toString());
        }
    }
}
