package com.boot.peterliu.redis.model.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

//NOTE:发红包记录实体
@Data
public class RedRecord implements Serializable{
    private Integer id;

    private Integer userId;//用户id

    private String redPacket;//红包全局唯一标识串

    private Integer total;//人数

    private BigDecimal amount;//总金额（单位为分）

    private Byte isActive=1;

    private Date createTime;

}