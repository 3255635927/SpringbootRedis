package com.boot.peterliu.redis.server.controller;

import com.boot.peterliu.redis.api.response.BaseResponse;
import com.boot.peterliu.redis.api.response.StatusCode;
import com.boot.peterliu.redis.server.service.ItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: PeterLiu
 * @Date: 2022/4/4 11:32
 * @Description:
 */
@Slf4j
@RestController
@RequestMapping("/item")
public class ItemController {
    @Autowired
    private ItemService itemService;

    //TODO:获取详情V1
    @GetMapping("/info")
    public BaseResponse getInfo(@RequestParam Integer id) {
        BaseResponse response = new BaseResponse(StatusCode.Success);
        try {
            response.setData(itemService.getItemInfo(id));
        } catch (Exception e) {
            log.error("商品详情controller-查看详情-发生异常:{}", e.getMessage());
            response = new BaseResponse(StatusCode.Failed.getCode(), e.getMessage());
        }
        return response;
    }

    //TODO:获取详情V2
    @GetMapping("/info/V2")
    public BaseResponse getInfoV2(@RequestParam Integer id) {
        BaseResponse response = new BaseResponse(StatusCode.Success);
        try {
            response.setData(itemService.getItemInfoV2(id));
        } catch (Exception e) {
            log.error("商品详情controller-查看详情V2-发生异常:{}", e.getMessage());
            response = new BaseResponse(StatusCode.Failed.getCode(), e.getMessage());
        }
        return response;
    }


    //TODO:删除
    @GetMapping("/delete")
    public BaseResponse delete(@RequestParam Integer id) {
        BaseResponse response = new BaseResponse(StatusCode.Success);
        try {
            itemService.deleteItem(id);
        } catch (Exception e) {
            log.error("商品详情controller-删除-发生异常:{}", e.getMessage());
            response = new BaseResponse(StatusCode.Failed.getCode(), e.getMessage());
        }
        return response;
    }


}







