package com.boot.peterliu.redis.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author: PeterLiu
 * @Date: 2022/3/6 21:05
 * @Description:保证手机号的唯一性
 */
@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class FareDto implements Serializable {

    public String phone;
}
