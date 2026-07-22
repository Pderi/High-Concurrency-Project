package com.hc.ticket.module.tkt.mq.consumer;

import com.hc.ticket.module.tkt.constants.ApiConstants;
import com.hc.ticket.module.tkt.mq.message.OrderCreateMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 异步建单消费者骨架（业务逻辑后续实现）
 * <p>默认关闭；启动参数加 --spring.profiles.active=mq 启用。
 */
@Slf4j
@Component
@ConditionalOnProperty(prefix = "tkt.rocketmq", name = "enabled", havingValue = "true")
@RocketMQMessageListener(
        topic = ApiConstants.TOPIC_ORDER_CREATE,
        consumerGroup = ApiConstants.CONSUMER_GROUP_ORDER_CREATE
)
public class OrderCreateConsumer implements RocketMQListener<OrderCreateMessage> {

    @Override
    public void onMessage(OrderCreateMessage message) {
        log.info("[OrderCreateConsumer] received messageId={}, userId={}, tierId={}, quantity={}",
                message.getMessageId(), message.getUserId(), message.getTierId(), message.getQuantity());
        // TODO: 幂等校验 + 事务落库（订单 / 票档 sold_stock / 流水）
    }
}
