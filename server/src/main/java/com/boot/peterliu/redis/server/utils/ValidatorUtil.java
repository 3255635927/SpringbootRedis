package com.boot.peterliu.redis.server.utils;

import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.List;
import java.util.function.Consumer;

/**
 * 统一校验前端参数的工具
 */
public class ValidatorUtil {

   //TODO:统一校验前端传过来的参数，并给出对应的错误提示
    public static String checkErrors(BindingResult result){
        StringBuilder sb = new StringBuilder(" ");
        //校验是否传入空参数(配置Validated注解手动触发判断)
        if(result.hasErrors()){
            //传统遍历写法1:
//            List<ObjectError> allErrors = result.getAllErrors();
//            for(ObjectError error:allErrors){
//                sb.append(error.getDefaultMessage()).append("; ");
//            }

            //以下是java8遍历写法2：
            result.getAllErrors().stream().forEach(error -> sb.append(error.getDefaultMessage()).append(";"));
        }
        return sb.toString();
    }


}
