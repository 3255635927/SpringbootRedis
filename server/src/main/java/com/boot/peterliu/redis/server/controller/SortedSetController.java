package com.boot.peterliu.redis.server.controller;

import cn.hutool.core.util.StrUtil;
import com.boot.peterliu.redis.api.response.BaseResponse;
import com.boot.peterliu.redis.api.response.StatusCode;
import com.boot.peterliu.redis.model.entity.PhoneFare;
import com.boot.peterliu.redis.server.service.SortedSetService;
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
 * SortedSet有序集合~场景实战~话费充值排行榜案列(version1)
 *
 * @Author: PeterLiu
 * @Date: 2022/2/28 21:02
 * @Description: 有序集合控制类
 */

@Log4j2
@RestController
@RequestMapping("/sorted/set")
public class SortedSetController {
    @Autowired
    private SortedSetService sortedSetService;

    //TODO:有序集合~测试新增话费
    @PostMapping(value = "/put", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public BaseResponse put(@RequestBody @Validated PhoneFare fare, BindingResult result) {
        String res = ValidatorUtil.checkErrors(result);
        if (StrUtil.isNotBlank(res)) {
            return new BaseResponse(StatusCode.Failed.getCode(), res);
        }
        BaseResponse response = new BaseResponse(StatusCode.Success);
        try {
            log.info("充值话费:{}", fare);
            response.setData(sortedSetService.addRecord(fare));
        } catch (Exception e) {
            response = new BaseResponse(StatusCode.Failed.getCode(), e.getMessage());
        }
        return response;
    }

    //TODO:有序集合~测试新增话费V2（版本2，优化逻辑）
    @PostMapping(value = "/put/v2", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public BaseResponse putV2(@RequestBody @Validated PhoneFare fare, BindingResult result) {
        String res = ValidatorUtil.checkErrors(result);
        if (StrUtil.isNotBlank(res)) {
            return new BaseResponse(StatusCode.Failed.getCode(), res);
        }
        BaseResponse response = new BaseResponse(StatusCode.Success);
        try {
            log.info("充值话费:{}", fare);
            response.setData(sortedSetService.addRecordV2(fare));
        } catch (Exception e) {
            response = new BaseResponse(StatusCode.Failed.getCode(), e.getMessage());
        }
        return response;
    }

    //TODO:按照排序获取话费充值记录
    @GetMapping(value = "/get")
    public BaseResponse get() {
        BaseResponse<Object> response = new BaseResponse<>(StatusCode.Success);
        Map<String, Object> resMap = Maps.newHashMap();
        try {
            resMap.put("sortedKeyASC", sortedSetService.getSortedFares(true));
            resMap.put("sortedKeyDESC", sortedSetService.getSortedFares(false));
        } catch (Exception e) {
            response = new BaseResponse(StatusCode.Failed.getCode(), e.getMessage());
        }
        response.setData(resMap);
        return response;
    }

    //TODO:按照排序获取话费充值记录 V2
    @GetMapping(value = "/get/v2")
    public BaseResponse getV2() {
        BaseResponse<Object> response = new BaseResponse<>(StatusCode.Success);
        try {
            response.setData(sortedSetService.getSortedFaresV2());
        } catch (Exception e) {
            response = new BaseResponse(StatusCode.Failed.getCode(), e.getMessage());
        }
        return response;
    }


}
