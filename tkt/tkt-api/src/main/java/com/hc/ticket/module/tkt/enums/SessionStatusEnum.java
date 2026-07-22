package com.hc.ticket.module.tkt.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 场次状态
 */
@Getter
@AllArgsConstructor
public enum SessionStatusEnum {

    DRAFT(0, "草稿"),
    COMING_SOON(1, "即将开售"),
    ON_SALE(2, "开售中"),
    STOP_SALE(3, "停售"),
    ENDED(4, "结束");

    private final Integer status;
    private final String name;
}
