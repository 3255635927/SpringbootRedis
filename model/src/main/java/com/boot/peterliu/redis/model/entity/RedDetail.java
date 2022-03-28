package com.boot.peterliu.redis.model.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

//NOTE:红包明细金额实体
@Data
public class RedDetail implements Serializable{

    private Integer id;

    private Integer recordId;//红包记录id

    private BigDecimal amount;//每个小红包的金额（单位为分）

    private Byte isActive=1;

    private Date createTime;

}