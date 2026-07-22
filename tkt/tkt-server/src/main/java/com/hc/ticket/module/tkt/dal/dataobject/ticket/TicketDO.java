package com.hc.ticket.module.tkt.dal.dataobject.ticket;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hc.ticket.framework.mybatis.core.dataobject.BaseDO;
import com.hc.ticket.module.tkt.enums.TicketStatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 电子票凭证
 */
@TableName("tkt_ticket")
@Data
@EqualsAndHashCode(callSuper = true)
public class TicketDO extends BaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;
    /** 票号，全局唯一 */
    private String ticketNo;
    /** 订单ID */
    private Long orderId;
    /** 用户ID */
    private Long userId;
    /** 场次ID */
    private Long sessionId;
    /** 票档ID */
    private Long tierId;
    /**
     * 票状态
     * 枚举 {@link TicketStatusEnum}
     */
    private Integer ticketStatus;
    /** 租户ID */
    private Long tenantId;
}
