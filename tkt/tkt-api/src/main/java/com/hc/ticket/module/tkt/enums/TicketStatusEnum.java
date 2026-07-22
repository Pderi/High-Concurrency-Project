package com.hc.ticket.module.tkt.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 电子票状态
 */
@Getter
@AllArgsConstructor
public enum TicketStatusEnum {

    INACTIVE(0, "未生效"),
    VALID(1, "有效"),
    VOIDED(2, "作废");

    private final Integer status;
    private final String name;
}
