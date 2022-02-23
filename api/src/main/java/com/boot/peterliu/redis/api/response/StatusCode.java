package com.boot.peterliu.redis.api.response;

import lombok.Data;

/**
 * 状态码枚举类
 */
public enum StatusCode {

    Success(200,"成功"),
    Failed(-1,"失败"),
    InvalidParams(-1001,"参数非法")
    ;
    private Integer code;
    private String msg;

    StatusCode(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
