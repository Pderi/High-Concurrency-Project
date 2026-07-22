package com.hc.ticket.framework.common.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hc.ticket.module.tkt.constants.ErrorCode;
import lombok.Data;

import java.io.Serializable;

/**
 * 统一响应包装
 */
@Data
public class CommonResult<T> implements Serializable {

    private Integer code;
    private String msg;
    private T data;

    public static <T> CommonResult<T> success(T data) {
        CommonResult<T> result = new CommonResult<>();
        result.code = 0;
        result.msg = "success";
        result.data = data;
        return result;
    }

    public static <T> CommonResult<T> error(Integer code, String msg) {
        CommonResult<T> result = new CommonResult<>();
        result.code = code;
        result.msg = msg;
        return result;
    }

    public static <T> CommonResult<T> error(ErrorCode errorCode) {
        return error(errorCode.getCode(), errorCode.getMsg());
    }

    @JsonIgnore
    public boolean isSuccess() {
        return code != null && code == 0;
    }
}
