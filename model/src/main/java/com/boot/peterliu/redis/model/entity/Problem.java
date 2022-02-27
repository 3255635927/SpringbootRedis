package com.boot.peterliu.redis.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode // 此注解会生成equals(Object other) 和 hashCode()方法。
public class Problem implements Serializable{
    private Integer id;

    private String title;

    private String choiceA;

    private String choiceB;

    private Byte isActive=1;

    private Byte orderBy=0;
}