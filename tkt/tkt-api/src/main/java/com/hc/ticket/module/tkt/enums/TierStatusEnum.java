package com.hc.ticket.module.tkt.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 票档上下架状态
 */
@Getter
@AllArgsConstructor
public enum TierStatusEnum {

    OFFLINE(0, "下架"),
    ONLINE(1, "上架");

    private final Integer status;
    private final String name;
}
