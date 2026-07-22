package com.hc.ticket.module.tkt.constants;

/**
 * API / MQ 相关常量
 */
public interface ApiConstants {

    String APP_API_PREFIX = "/app-api/tkt";
    String ADMIN_API_PREFIX = "/admin-api/tkt";

    /** 异步建单 Topic */
    String TOPIC_ORDER_CREATE = "TKT_ORDER_CREATE";
    /** 补偿 Topic（可选） */
    String TOPIC_ORDER_COMPENSATE = "TKT_ORDER_COMPENSATE";

    String CONSUMER_GROUP_ORDER_CREATE = "tkt-order-create-consumer";
}
