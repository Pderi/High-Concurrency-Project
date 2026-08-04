package com.hc.ticket.module.tkt.mq.producer;

import com.hc.ticket.module.tkt.constants.ApiConstants;
import com.hc.ticket.module.tkt.mq.message.OrderCreateMessage;
import com.hc.ticket.module.tkt.service.order.GrabResultService;
import com.hc.ticket.module.tkt.service.stock.TierStockRedisService;
import com.hc.ticket.module.tkt.service.stock.UserBuyLimitRedisService;
import jakarta.annotation.Resource;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

/**
 * MQ 开启时：投递异步建单消息；发送失败回补 Redis
 */
@Component
@ConditionalOnProperty(prefix = "tkt.rocketmq", name = "enabled", havingValue = "true")
public class MqOrderCreatePublisher implements OrderCreatePublisher {

    @Resource
    private RocketMQTemplate rocketMQTemplate;
    @Resource
    private TierStockRedisService tierStockRedisService;
    @Resource
    private UserBuyLimitRedisService userBuyLimitRedisService;
    @Resource
    private GrabResultService grabResultService;

    @Override
    public void publish(OrderCreateMessage message) {
        try {
            rocketMQTemplate.syncSend(
                    ApiConstants.TOPIC_ORDER_CREATE,
                    MessageBuilder.withPayload(message).build());
        } catch (RuntimeException ex) {
            tierStockRedisService.rollback(message.getTierId(), message.getQuantity());
            userBuyLimitRedisService.rollback(
                    message.getUserId(), message.getSessionId(), message.getTierId(), message.getQuantity());
            grabResultService.saveFail(message.getMessageId(), 500, "消息投递失败");
            throw ex;
        }
    }
}
