package com.hc.ticket.module.tkt.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 订单状态
 */
@Getter
@AllArgsConstructor
public enum OrderStatusEnum {

    WAIT_PAY(10, "待支付"),
    PAID(20, "已支付"),
    CLOSED(30, "已关闭"),
    REFUNDED(40, "已退款");

    private final Integer status;
    private final String name;
}
