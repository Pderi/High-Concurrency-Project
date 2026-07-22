package com.hc.ticket.framework.common.exception;

import com.hc.ticket.module.tkt.constants.ErrorCode;

/**
 * 业务异常工具
 */
public final class ServiceExceptionUtil {

    private ServiceExceptionUtil() {
    }

    public static ServiceException exception(ErrorCode errorCode) {
        return new ServiceException(errorCode);
    }
}
