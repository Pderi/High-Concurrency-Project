package com.hc.ticket.module.tkt.dal.dataobject.order;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hc.ticket.framework.mybatis.core.dataobject.BaseDO;
import com.hc.ticket.module.tkt.enums.OrderStatusEnum;
import com.hc.ticket.module.tkt.enums.PayChannelEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 订单
 */
@TableName("tkt_order")
@Data
@EqualsAndHashCode(callSuper = true)
public class OrderDO extends BaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;
    /** 业务订单号，全局唯一 */
    private String orderNo;
    /** 用户ID */
    private Long userId;
    /** 场次ID */
    private Long sessionId;
    /** 票档ID */
    private Long tierId;
    /** 购买张数 */
    private Integer quantity;
    /** 下单时单价快照（分） */
    private Integer unitPriceCent;
    /** 应付总金额（分） */
    private Integer totalAmountCent;
    /**
     * 订单状态
     * 枚举 {@link OrderStatusEnum}
     */
    private Integer orderStatus;
    /**
     * 支付渠道
     * 枚举 {@link PayChannelEnum}
     */
    private Integer payChannel;
    /** 支付成功时间 */
    private LocalDateTime payTime;
    /** 支付截止时间 */
    private LocalDateTime payDeadline;
    /** 关闭原因 timeout/cancel 等 */
    private String closeReason;
    /** 客户端幂等键 */
    private String idempotencyKey;
    /** 租户ID */
    private Long tenantId;
}
