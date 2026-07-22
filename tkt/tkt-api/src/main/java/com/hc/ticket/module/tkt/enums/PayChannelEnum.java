package com.hc.ticket.module.tkt.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 支付渠道
 */
@Getter
@AllArgsConstructor
public enum PayChannelEnum {

    NONE(0, "未支付"),
    MOCK(1, "模拟支付"),
    WECHAT(2, "微信"),
    ALIPAY(3, "支付宝");

    private final Integer channel;
    private final String name;
}
