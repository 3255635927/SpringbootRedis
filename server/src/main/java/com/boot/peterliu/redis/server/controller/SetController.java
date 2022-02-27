package com.boot.peterliu.redis.server.controller;

import cn.hutool.core.util.StrUtil;
import com.boot.peterliu.redis.api.response.BaseResponse;
import com.boot.peterliu.redis.api.response.StatusCode;
import com.boot.peterliu.redis.model.entity.User;
import com.boot.peterliu.redis.server.service.SetService;
import com.boot.peterliu.redis.server.utils.ValidatorUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: PeterLiu
 * @Date: 2022/2/26 13:50
 * @Description:
 */
@RestController
@Log4j2
@RequestMapping("/set")
public class SetController {
    @Autowired
    private SetService setService;

    //TODO:提交用户注册信息
    @PostMapping(value = "/put", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public BaseResponse put(@RequestBody @Validated User user, BindingResult result) {
        String checkRes = ValidatorUtil.checkErrors(result);
        if (StrUtil.isNotBlank(checkRes)) {
            return new BaseResponse(StatusCode.Failed.getCode(), checkRes);
        }
        BaseResponse response = new BaseResponse(StatusCode.Success);
        try {
            log.info("用户注册信息:{}", user);
            response.setData(setService.registerUser(user));
        } catch (Exception e) {
            response = new BaseResponse(StatusCode.Failed.getCode(), e.getMessage());
        }
        return response;
    }

    //TODO:取出缓存中集合Set中所有注册用户的信息
    @GetMapping("/get")
    public BaseResponse get() {
        BaseResponse<Object> response = new BaseResponse<>(StatusCode.Success);
        try {
            response.setData(setService.getEmails());
        } catch (Exception e) {
            response = new BaseResponse<>(StatusCode.Failed.getCode(), e.getMessage());
        }
        return response;
    }

    //TODO:取出随机问题库随机弹出的问题
    @GetMapping("/problem/random")
    public BaseResponse getProblem() {
        BaseResponse response = new BaseResponse<>(StatusCode.Success);
        try {
            response.setData(setService.getRandomProblem());
        } catch (Exception e) {
            response = new BaseResponse(StatusCode.Failed.getCode(), e.getMessage());
        }
        return response;
    }


}
