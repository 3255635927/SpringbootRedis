package com.boot.peterliu.redis.server.controller;

import cn.hutool.core.util.StrUtil;
import com.boot.peterliu.redis.api.response.BaseResponse;
import com.boot.peterliu.redis.api.response.StatusCode;
import com.boot.peterliu.redis.model.entity.SysConfig;
import com.boot.peterliu.redis.server.service.HashService;
import com.boot.peterliu.redis.server.utils.ValidatorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: PeterLiu
 * @Date: 2022/3/7 18:02
 * @Description: 哈希HASH
 */
@RestController
@RequestMapping("/hash")
public class HashController {
    @Autowired
    private HashService hashService;

    //TODO:存放数据
    @PostMapping(value = "/put", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public BaseResponse putHash(@RequestBody @Validated SysConfig config, BindingResult result) {
        BaseResponse<SysConfig> response = new BaseResponse<>(StatusCode.Success);
        String errors = ValidatorUtil.checkErrors(result);
        if (StrUtil.isNotBlank(errors)) {
            return new BaseResponse(StatusCode.Failed);
        }
        try {
            hashService.addSysConfig(config);
        } catch (Exception e) {
            response = new BaseResponse(StatusCode.Failed.getCode(), e.getMessage());
        }
        return response;
    }

    //TODO:获取所有数据字典配置列表
    @GetMapping("/get")
    public BaseResponse get() {
        BaseResponse<Object> response = new BaseResponse<>(StatusCode.Success);
        try {
            response.setData(hashService.getAllCacheConfigs());
        } catch (Exception e) {
            response = new BaseResponse(StatusCode.Failed.getCode(), e.getMessage());
        }
        return response;
    }

    //TODO:获取某一类型下所有的列表
    @GetMapping("/get/type")
    public BaseResponse getByType(String type) {
        BaseResponse<Object> response = new BaseResponse<>(StatusCode.Success);
        try {
            response.setData(hashService.getDataByType(type));
        } catch (Exception e) {
            response = new BaseResponse(StatusCode.Failed.getCode(), e.getMessage());
        }
        return response;
    }

}
