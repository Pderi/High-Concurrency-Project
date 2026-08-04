package com.hc.ticket.module.tkt.mq.consumer;

import com.hc.ticket.framework.common.exception.ServiceException;
import com.hc.ticket.module.tkt.constants.ApiConstants;
import com.hc.ticket.module.tkt.mq.message.OrderCreateMessage;
import com.hc.ticket.module.tkt.service.order.CreateOrderResult;
import com.hc.ticket.module.tkt.service.order.GrabResultService;
import com.hc.ticket.module.tkt.service.order.OrderCreateService;
import com.hc.ticket.module.tkt.service.stock.TierStockRedisService;
import com.hc.ticket.module.tkt.service.stock.UserBuyLimitRedisService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 异步建单消费者
 */
@Slf4j
@Component
@ConditionalOnProperty(prefix = "tkt.rocketmq", name = "enabled", havingValue = "true")
@RocketMQMessageListener(
        topic = ApiConstants.TOPIC_ORDER_CREATE,
        consumerGroup = ApiConstants.CONSUMER_GROUP_ORDER_CREATE
)
public class OrderCreateConsumer implements RocketMQListener<OrderCreateMessage> {

    @Resource
    private OrderCreateService orderCreateService;
    @Resource
    private GrabResultService grabResultService;
    @Resource
    private TierStockRedisService tierStockRedisService;
    @Resource
    private UserBuyLimitRedisService userBuyLimitRedisService;

    @Override
    public void onMessage(OrderCreateMessage message) {
        log.info("[OrderCreateConsumer] messageId={}, userId={}, tierId={}, quantity={}",
                message.getMessageId(), message.getUserId(), message.getTierId(), message.getQuantity());
        try {
            CreateOrderResult result = orderCreateService.createOrder(message);
            if (result.isRollbackRedis()) {
                rollbackRedisOccupy(message);
            }
            grabResultService.saveSuccess(message.getMessageId(), result.getOrderNo());
        } catch (ServiceException ex) {
            rollbackRedisOccupy(message);
            grabResultService.saveFail(message.getMessageId(), ex.getCode(), ex.getMessage());
            throw ex;
        }
    }

    private void rollbackRedisOccupy(OrderCreateMessage message) {
        tierStockRedisService.rollback(message.getTierId(), message.getQuantity());
        userBuyLimitRedisService.rollback(
                message.getUserId(), message.getSessionId(), message.getTierId(), message.getQuantity());
    }
}
