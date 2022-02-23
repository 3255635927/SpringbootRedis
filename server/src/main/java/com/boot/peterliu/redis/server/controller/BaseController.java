package com.boot.peterliu.redis.server.controller;

import com.boot.peterliu.redis.api.response.BaseResponse;
import com.boot.peterliu.redis.api.response.StatusCode;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/base")
@Log4j
public class BaseController {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static final String TestRedisKey = "SpringbootRedis:HelloWorld";

    @RequestMapping("/info")
    public String info() {
        String str = "redis技术入门与典型应用实战";
        return str;
    }

    @PostMapping("/test/put")
    public BaseResponse testPut(@RequestParam String testName) {
        BaseResponse response = new BaseResponse(StatusCode.Success);
        try{
            stringRedisTemplate.opsForValue().set(TestRedisKey, testName);
            response.setData("hello world");
        }catch (Exception e){
            log.info("----put测试异常信息：",e.fillInStackTrace());
            response=new BaseResponse(StatusCode.Failed.getCode(),StatusCode.Failed.getMsg());
        }
        return response;
    }

    @GetMapping(value = "/test/get")
    public BaseResponse testGet(){
        BaseResponse<Object> response = new BaseResponse<>(StatusCode.Success);
        try {
            response.setData(stringRedisTemplate.opsForValue().get(TestRedisKey));
        }catch (Exception e){
            log.info("----get测试异常信息：",e.fillInStackTrace());
            response=new BaseResponse(StatusCode.Failed.getCode(),StatusCode.Failed.getMsg());
        }
        return response;
    }
}
