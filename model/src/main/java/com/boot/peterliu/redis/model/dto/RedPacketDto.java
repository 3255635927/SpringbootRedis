package com.boot.peterliu.redis.model.dto;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

/**
 * @Author: PeterLiu
 * @Date: 2022/3/23 15:08
 * @Description:红包分配的数据传输对象DTO
 */
@Data
public class RedPacketDto {
    @NotNull
    private Integer userId;//note:发红包的用户id
    @NotNull(message = "红包总金额不能为空")
    private Integer amount;//note:输入的红包金额
    @NotNull(message = "总人数不能为空")
    private Integer people;//note:输入的可领取红包的人数，单位为分

}
