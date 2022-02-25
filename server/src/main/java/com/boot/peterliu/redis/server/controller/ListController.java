package com.boot.peterliu.redis.server.controller;

import cn.hutool.core.util.StrUtil;
import com.boot.peterliu.redis.api.response.BaseResponse;
import com.boot.peterliu.redis.api.response.StatusCode;
import com.boot.peterliu.redis.model.entity.Notice;
import com.boot.peterliu.redis.model.entity.Product;
import com.boot.peterliu.redis.server.service.ListService;
import com.boot.peterliu.redis.server.utils.ValidatorUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/list")
@Log4j2
public class ListController {
    @Autowired
    private ListService listService;

    /**
     * 添加商品
     *
     * @return
     */
    @PostMapping(value = "/put", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public BaseResponse put(@RequestBody Product product, BindingResult result) {
        BaseResponse<Object> response = new BaseResponse<>(StatusCode.Success);
        try {
            log.info("商户商品信息:{}", product);
            response.setData(listService.addProduct(product));
        } catch (Exception e) {
            log.error("List实战-添加-商户商品~发生异常：{}", e.fillInStackTrace());
            response = new BaseResponse<>(StatusCode.Failed.getCode(), e.getMessage());
        }
        return response;
    }

    /**
     * 也是添加商户商品信息
     *
     * @param product
     * @param result
     * @return
     */
    @PostMapping(value = "/put/v2", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public BaseResponse putV2(@RequestBody @Validated Product product, BindingResult result) {
        //统一校验
        String checkRes = ValidatorUtil.checkErrors(result);
        if (StrUtil.isNotBlank(checkRes)) {
            return new BaseResponse(StatusCode.InvalidParams.getCode(), checkRes);
        }
        BaseResponse<Object> response = new BaseResponse<>(StatusCode.Success);
        try {
            log.info("商户商品信息V2:{}", product);

        } catch (Exception e) {
            log.error("List实战-添加-商户商品~发生异常：{}", e.fillInStackTrace());
            response = new BaseResponse<>(StatusCode.Failed.getCode(), e.getMessage());
        }
        return response;
    }


    /**
     * 获取商户的商品列表
     *
     * @return
     */
    @GetMapping("/get")
    public BaseResponse get(@RequestParam("userId") Integer userId) {
        BaseResponse<Object> response = new BaseResponse<>(StatusCode.Success);
        try {
            response.setData(listService.getHistoryProduct(userId));
        } catch (Exception e) {
            log.error("List实战-获取-商户商品列表~发生异常：{}", e.fillInStackTrace());
            response = new BaseResponse<>(StatusCode.Failed.getCode(), e.getMessage());
        }
        return response;
    }

    /**
     * 平台发送通知信息给各个商户
     *
     * @param notice
     * @param result
     * @return
     */
    @PostMapping(value = "/notice/put", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public BaseResponse putNotice(@RequestBody @Validated Notice notice, BindingResult result) {
        String checkRes = ValidatorUtil.checkErrors(result);
        if (StrUtil.isNotBlank(checkRes)) {
            return new BaseResponse(StatusCode.InvalidParams.getCode(), checkRes);
        }
        BaseResponse<Object> response = new BaseResponse<>(StatusCode.Success);
        try {
            log.info("平台发送通知给各位商户:{}", notice);
            listService.pushNotice(notice);
        } catch (Exception e) {
            response = new BaseResponse<>(StatusCode.Failed.getCode(), e.getMessage());
        }
        return response;
    }


}