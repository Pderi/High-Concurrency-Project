package com.hc.ticket.module.tkt.dal.dataobject.stock;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hc.ticket.module.tkt.enums.StockChangeTypeEnum;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 库存流水（无逻辑删除 / updater 字段，不继承 BaseDO）
 */
@TableName("tkt_stock_ledger")
@Data
public class StockLedgerDO implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;
    /** 票档ID */
    private Long tierId;
    /** 场次ID（冗余） */
    private Long sessionId;
    /**
     * 变动类型
     * 枚举 {@link StockChangeTypeEnum}
     */
    private Integer changeType;
    /** sold_stock 变动量 */
    private Integer delta;
    /** 变动前 sold_stock */
    private Integer beforeSold;
    /** 变动后 sold_stock */
    private Integer afterSold;
    /** 关联订单 */
    private Long orderId;
    /** 备注 */
    private String remark;
    /** 操作者或 system */
    private String creator;
    private LocalDateTime createTime;
    /** 租户ID */
    private Long tenantId;
}
