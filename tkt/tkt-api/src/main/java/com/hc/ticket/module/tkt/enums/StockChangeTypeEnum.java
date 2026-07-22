package com.hc.ticket.module.tkt.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 库存流水变动类型
 */
@Getter
@AllArgsConstructor
public enum StockChangeTypeEnum {

    ORDER_RESERVE(1, "下单预占"),
    PAY_CONFIRM(2, "支付确认"),
    TIMEOUT_RELEASE(3, "超时释放"),
    ADMIN_ADJUST(4, "管理端调整"),
    RECONCILE_FIX(5, "对账修正");

    private final Integer type;
    private final String name;
}
