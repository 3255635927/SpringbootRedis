package com.boot.peterliu.redis.server.controller;

import com.boot.peterliu.redis.api.response.BaseResponse;
import com.boot.peterliu.redis.api.response.StatusCode;
import com.boot.peterliu.redis.model.entity.Product;
import com.boot.peterliu.redis.server.service.ListService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
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
            log.info("商户商品信息:{}",product);
            response.setData(listService.addProduct(product));
        } catch (Exception e) {
            log.error("List实战-添加-商户商品~发生异常：{}", e.fillInStackTrace());
            response = new BaseResponse<>(StatusCode.Failed.getCode(), e.getMessage());
        }
        return response;
    }

    /**
     * 获取商户的商品列表
     * @return
     */
    @GetMapping("/get")
    public BaseResponse get(@RequestParam("userId") Integer userId){
        BaseResponse<Object> response = new BaseResponse<>(StatusCode.Success);
        try {
            response.setData(listService.getHistoryProduct(userId));
        } catch (Exception e) {
            log.error("List实战-获取-商户商品列表~发生异常：{}", e.fillInStackTrace());
            response = new BaseResponse<>(StatusCode.Failed.getCode(), e.getMessage());
        }
        return response;

    }



}