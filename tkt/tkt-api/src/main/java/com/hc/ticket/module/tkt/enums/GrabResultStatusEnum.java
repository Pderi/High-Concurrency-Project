package com.hc.ticket.module.tkt.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 抢票受理结果状态（Redis 轮询）
 */
@Getter
@AllArgsConstructor
public enum GrabResultStatusEnum {

    PENDING(0, "处理中"),
    SUCCESS(1, "成功"),
    FAIL(2, "失败");

    private final Integer status;
    private final String name;
}
