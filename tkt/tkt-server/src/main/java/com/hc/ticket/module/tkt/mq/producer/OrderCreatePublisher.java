package com.hc.ticket.module.tkt.mq.producer;

import com.hc.ticket.module.tkt.mq.message.OrderCreateMessage;

/**
 * 建单消息投递（MQ 或本地同步）
 */
public interface OrderCreatePublisher {

    void publish(OrderCreateMessage message);
}
