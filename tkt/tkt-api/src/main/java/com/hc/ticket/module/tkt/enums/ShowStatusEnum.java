package com.hc.ticket.module.tkt.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 演出状态
 */
@Getter
@AllArgsConstructor
public enum ShowStatusEnum {

    DRAFT(0, "草稿"),
    PUBLISHED(1, "已发布"),
    OFFLINE(2, "已下架");

    private final Integer status;
    private final String name;
}
