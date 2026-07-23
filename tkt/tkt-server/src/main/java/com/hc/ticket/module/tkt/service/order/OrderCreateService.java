package com.hc.ticket.module.tkt.service.order;

import com.hc.ticket.module.tkt.mq.message.OrderCreateMessage;

/**
 * 异步/同步建单落库
 */
public interface OrderCreateService {

    /**
     * 幂等建单：插订单 + 增 sold_stock + 写流水
     */
    CreateOrderResult createOrder(OrderCreateMessage message);
}
