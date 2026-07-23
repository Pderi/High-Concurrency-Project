package com.hc.ticket.module.tkt.mq.producer;

import com.hc.ticket.framework.common.exception.ServiceException;
import com.hc.ticket.module.tkt.mq.message.OrderCreateMessage;
import com.hc.ticket.module.tkt.service.order.CreateOrderResult;
import com.hc.ticket.module.tkt.service.order.GrabResultService;
import com.hc.ticket.module.tkt.service.order.OrderCreateService;
import com.hc.ticket.module.tkt.service.stock.TierStockRedisService;
import jakarta.annotation.Resource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * MQ 关闭时：进程内同步建单（便于本地联调）
 */
@Component
@ConditionalOnProperty(prefix = "tkt.rocketmq", name = "enabled", havingValue = "false", matchIfMissing = true)
public class SyncOrderCreatePublisher implements OrderCreatePublisher {

    @Resource
    private OrderCreateService orderCreateService;
    @Resource
    private GrabResultService grabResultService;
    @Resource
    private TierStockRedisService tierStockRedisService;

    @Override
    public void publish(OrderCreateMessage message) {
        try {
            CreateOrderResult result = orderCreateService.createOrder(message);
            if (result.isRollbackRedis()) {
                tierStockRedisService.rollback(message.getTierId(), message.getQuantity());
            }
            grabResultService.saveSuccess(message.getMessageId(), result.getOrderNo());
        } catch (ServiceException ex) {
            tierStockRedisService.rollback(message.getTierId(), message.getQuantity());
            grabResultService.saveFail(message.getMessageId(), ex.getCode(), ex.getMessage());
            throw ex;
        }
    }
}
