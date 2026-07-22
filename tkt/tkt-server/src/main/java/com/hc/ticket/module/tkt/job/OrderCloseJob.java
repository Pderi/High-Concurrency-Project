package com.hc.ticket.module.tkt.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 超时关单任务骨架（后续补分布式锁与批处理）
 */
@Slf4j
@Component
public class OrderCloseJob {

    @Scheduled(cron = "0 */1 * * * ?")
    public void closeExpiredOrders() {
        log.debug("[OrderCloseJob] tick - close expired unpaid orders (TODO)");
    }
}
