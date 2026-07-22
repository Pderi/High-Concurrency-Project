package com.hc.ticket.module.tkt.mq.message;

import lombok.Data;

import java.io.Serializable;

/**
 * 异步建单消息体（对齐设计文档）
 */
@Data
public class OrderCreateMessage implements Serializable {

    /** MQ 层唯一，用于去重 */
    private String messageId;
    private Long userId;
    private Long sessionId;
    private Long tierId;
    private Integer quantity;
    /** 单价快照（分） */
    private Integer unitPriceCent;
    /** 客户端幂等键，可空 */
    private String idempotencyKey;
    /** Redis 预扣凭证，可空 */
    private String redisToken;
}
