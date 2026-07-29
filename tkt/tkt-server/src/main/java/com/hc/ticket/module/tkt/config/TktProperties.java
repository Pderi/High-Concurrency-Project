package com.hc.ticket.module.tkt.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 票务业务配置项
 */
@Data
@Component
@ConfigurationProperties(prefix = "tkt")
public class TktProperties {

    private final Order order = new Order();
    private final Grab grab = new Grab();
    private final Job job = new Job();
    private final Cache cache = new Cache();
    private final Rocketmq rocketmq = new Rocketmq();
    private final RateLimit rateLimit = new RateLimit();
    private final Admin admin = new Admin();

    @Data
    public static class Order {
        /** 支付倒计时分钟数 */
        private Integer payTimeoutMinutes = 15;
    }

    @Data
    public static class Grab {
        /** 客户端轮询间隔建议（秒） */
        private Integer asyncPollSeconds = 1;
        /** 受理结果缓存 TTL（分钟） */
        private Integer resultTtlMinutes = 30;
    }

    @Data
    public static class Job {
        private final CloseOrder closeOrder = new CloseOrder();
        private final Reconcile reconcile = new Reconcile();

        @Data
        public static class CloseOrder {
            private Integer batchSize = 100;
        }

        @Data
        public static class Reconcile {
            /** 是否启用对账 Job（默认 true） */
            private Boolean enabled = true;
        }
    }

    @Data
    public static class Cache {
        /** 演出/场次/票档元数据 Redis TTL（秒）；默认 7 天；管理端写后 SET 刷新 */
        private Integer metaTtlSeconds = 7 * 24 * 3600;
    }

    @Data
    public static class Rocketmq {
        /** 是否启用 RocketMQ 消费者/生产者自动配置侧能力 */
        private Boolean enabled = false;
    }

    @Data
    public static class RateLimit {
        /** 是否启用抢票接口限流 */
        private Boolean enabled = true;
        /** 每个刷新周期内允许的请求数 */
        private Integer limitForPeriod = 100;
        /** 刷新周期秒数 */
        private Integer limitRefreshPeriodSeconds = 1;
    }

    @Data
    public static class Admin {
        /** 是否启用管理端 Token 校验 */
        private Boolean authEnabled = true;
        /** 管理端请求头 X-Admin-Token 期望值 */
        private String token = "dev-admin-token";
    }
}
