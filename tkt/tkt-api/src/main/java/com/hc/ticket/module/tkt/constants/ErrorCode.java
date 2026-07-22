package com.hc.ticket.module.tkt.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 错误码
 */
@Getter
@AllArgsConstructor
public class ErrorCode {

    private final Integer code;
    private final String msg;
}
