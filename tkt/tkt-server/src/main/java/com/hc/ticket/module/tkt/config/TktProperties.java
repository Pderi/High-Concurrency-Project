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

        @Data
        public static class CloseOrder {
            private Integer batchSize = 100;
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
}
