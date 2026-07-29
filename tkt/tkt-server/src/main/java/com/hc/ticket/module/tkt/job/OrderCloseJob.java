package com.hc.ticket.module.tkt.job;

import com.hc.ticket.module.tkt.config.TktProperties;
import com.hc.ticket.module.tkt.redis.RedisLockHelper;
import com.hc.ticket.module.tkt.redis.TktRedisKeys;
import com.hc.ticket.module.tkt.service.order.OrderCloseService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * 超时关单任务：扫待支付且已过 pay_deadline 的订单，释放库存
 */
@Slf4j
@Component
public class OrderCloseJob {

    @Resource
    private OrderCloseService orderCloseService;
    @Resource
    private TktProperties tktProperties;
    @Resource
    private RedisLockHelper redisLockHelper;

    @Scheduled(cron = "0 */1 * * * ?")
    public void closeExpiredOrders() {
        String token = redisLockHelper.tryLock(TktRedisKeys.jobCloseOrder(), Duration.ofSeconds(50));
        if (token == null) {
            log.debug("[OrderCloseJob] skip, lock held by another instance");
            return;
        }
        try {
            Integer batchSize = tktProperties.getJob().getCloseOrder().getBatchSize();
            int closed = orderCloseService.closeExpiredOrders(batchSize == null ? 100 : batchSize);
            if (closed > 0) {
                log.info("[OrderCloseJob] closed {} expired unpaid orders", closed);
            }
        } finally {
            redisLockHelper.unlock(TktRedisKeys.jobCloseOrder(), token);
        }
    }
}
