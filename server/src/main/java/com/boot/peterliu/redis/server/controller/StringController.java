package com.boot.peterliu.redis.server.controller;

import com.boot.peterliu.redis.api.response.BaseResponse;
import com.boot.peterliu.redis.api.response.StatusCode;
import com.boot.peterliu.redis.model.entity.Item;
import com.boot.peterliu.redis.server.service.StringService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 字符串String实战-商品详情的存储
 */
@Log4j2
@RestController
@RequestMapping("/String")
public class StringController {
    @Autowired
    private StringService stringService;

    //添加商品详情
    @PostMapping(value = "/put", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public BaseResponse put(@RequestBody @Validated Item item, BindingResult result) {
        if(result.hasErrors()){
            return new BaseResponse(StatusCode.InvalidParams);
        }
        BaseResponse response = new BaseResponse(StatusCode.Success);
        try {
            log.info("商品信息:{}",item);
            stringService.addItem(item);
        } catch (Exception e) {
            log.error("字符串String实战-商品详情添加出现异常");
            response = new BaseResponse(StatusCode.Failed.getCode(), e.getMessage());
        }
        return response;
    }

    /**
     * 获取商品详情
     *
     * @return
     */
    @GetMapping("/get")
    public BaseResponse get(@RequestParam Integer id) {
        BaseResponse<Object> response = new BaseResponse<>(StatusCode.Success);
        try {
            response.setData(stringService.getItem(id));//从缓存中取出数据结果
        } catch (Exception e) {
            log.error("字符串String实战-获取商品详情出现异常");
            response = new BaseResponse<>(StatusCode.Failed.getCode(), e.getMessage());
        }
        return response;
    }


}
