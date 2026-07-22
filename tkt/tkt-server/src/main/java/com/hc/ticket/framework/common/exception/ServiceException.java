package com.hc.ticket.framework.common.exception;

import com.hc.ticket.module.tkt.constants.ErrorCode;
import lombok.Getter;

/**
 * 业务异常
 */
@Getter
public class ServiceException extends RuntimeException {

    private final Integer code;

    public ServiceException(ErrorCode errorCode) {
        super(errorCode.getMsg());
        this.code = errorCode.getCode();
    }

    public ServiceException(Integer code, String message) {
        super(message);
        this.code = code;
    }
}
