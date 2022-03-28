package com.boot.peterliu.redis.model.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

//NOTE:抢红包记录实体
@Data
public class RedRobRecord implements Serializable{
    private Integer id;

    private Integer userId;//用户账号

    private String redPacket;//红包标识串

    private BigDecimal amount;//红包金额（单位为分）

    private Date robTime;//抢红包时间

    private Byte isActive=1;

}