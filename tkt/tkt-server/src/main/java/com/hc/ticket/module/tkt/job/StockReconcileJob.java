package com.hc.ticket.module.tkt.job;

import com.hc.ticket.module.tkt.config.TktProperties;
import com.hc.ticket.module.tkt.redis.RedisLockHelper;
import com.hc.ticket.module.tkt.redis.TktRedisKeys;
import com.hc.ticket.module.tkt.service.stock.StockReconcileService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * 库存对账最小版：比对 sold_stock 与有效订单占用
 */
@Slf4j
@Component
public class StockReconcileJob {

    @Resource
    private StockReconcileService stockReconcileService;
    @Resource
    private RedisLockHelper redisLockHelper;
    @Resource
    private TktProperties tktProperties;

    @Scheduled(cron = "0 */5 * * * ?")
    public void reconcile() {
        Boolean enabled = tktProperties.getJob().getReconcile().getEnabled();
        if (Boolean.FALSE.equals(enabled)) {
            return;
        }
        String token = redisLockHelper.tryLock(TktRedisKeys.jobReconcile(), Duration.ofMinutes(4));
        if (token == null) {
            log.debug("[StockReconcileJob] skip, lock held by another instance");
            return;
        }
        try {
            int fixed = stockReconcileService.reconcileAll();
            if (fixed > 0) {
                log.info("[StockReconcileJob] fixed {} tiers", fixed);
            }
        } finally {
            redisLockHelper.unlock(TktRedisKeys.jobReconcile(), token);
        }
    }
}
