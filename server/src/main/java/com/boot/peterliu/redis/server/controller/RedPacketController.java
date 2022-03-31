package com.boot.peterliu.redis.server.controller;

import cn.hutool.core.util.StrUtil;
import com.boot.peterliu.redis.api.response.BaseResponse;
import com.boot.peterliu.redis.api.response.StatusCode;
import com.boot.peterliu.redis.model.dto.RedPacketDto;
import com.boot.peterliu.redis.server.service.RedPacketService;
import com.boot.peterliu.redis.server.utils.ValidatorUtil;
import com.google.common.collect.Maps;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: PeterLiu
 * @Date: 2022/3/23 15:08
 * @Description:
 */
@Log4j2
@RestController
@RequestMapping("/red/packet")
public class RedPacketController {
    @Autowired
    private RedPacketService redPacketService;

    //NOTE:分发红包
    @PostMapping(value = "/distribute", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public BaseResponse distributePacket(@RequestBody @Validated RedPacketDto redPacketDto, BindingResult result) {
        BaseResponse response = new BaseResponse<>(StatusCode.Success);
        String errors = ValidatorUtil.checkErrors(result);
        if (StrUtil.isNotBlank(errors)) {
            return new BaseResponse(StatusCode.Failed.getCode(), errors);
        }
        Map<String, Object> map = Maps.newHashMap();
        try {
            String packetKey = redPacketService.distributeRedPacket(redPacketDto);
            map.put("redKey",packetKey);
        } catch (Exception e) {
            log.error("发红包~业务模块发生异常：{}", e.fillInStackTrace());
            response = new BaseResponse(StatusCode.Failed.getCode(), e.getMessage());
        }
        return response;
    }

    //NOTE:抢红包
    @GetMapping("/rob")
    public BaseResponse robRedPacket(@RequestParam Integer userId, @RequestParam String redId) {
        BaseResponse response = new BaseResponse<>(StatusCode.Success);
        try {

        } catch (Exception e) {
            log.error("发红包~业务模块发生异常：userId={},redId={}", userId, redId, e.fillInStackTrace());
            response = new BaseResponse(StatusCode.Failed.getCode(), e.getMessage());
        }
        return response;
    }


}
