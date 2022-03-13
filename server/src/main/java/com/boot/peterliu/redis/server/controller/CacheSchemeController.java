package com.boot.peterliu.redis.server.controller;


import com.boot.peterliu.redis.api.response.BaseResponse;
import com.boot.peterliu.redis.api.response.StatusCode;
import com.boot.peterliu.redis.server.service.CacheSchemeService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: PeterLiu
 * @Date: 2022/3/13 12:50
 * @Description: 解决缓存穿透、击穿、雪崩等问题的方案
 */
@Log4j2
@RestController
@RequestMapping("/cache/scheme")
public class CacheSchemeController {
    @Autowired
    private CacheSchemeService cacheSchemeService;

    //TODO:测试缓存穿透
    @GetMapping("/penetrate")
    public BaseResponse get(@RequestParam Integer id) {
        BaseResponse response = new BaseResponse<>(StatusCode.Success);
        try {
//            response.setData(cacheSchemeService.getItem(id));
            response.setData(cacheSchemeService.getItemV2(id));
        } catch (Exception e) {
            log.info("典型应用场景-缓存穿透-发生异常：{}", e.fillInStackTrace());
            response = new BaseResponse(StatusCode.Failed.getCode(), e.getMessage());
        }
        return response;
    }

}
















