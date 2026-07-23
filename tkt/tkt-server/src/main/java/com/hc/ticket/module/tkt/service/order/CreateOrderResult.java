package com.hc.ticket.module.tkt.service.order;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateOrderResult {

    private String orderNo;
    /** true：本请求未真正占用 DB 库存（幂等命中），需回补本次 Redis 预扣 */
    private boolean rollbackRedis;
}
