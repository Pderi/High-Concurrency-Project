package com.hc.ticket.module.tkt.dal.dataobject.tier;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import com.hc.ticket.framework.mybatis.core.dataobject.BaseDO;
import com.hc.ticket.module.tkt.enums.TierStatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 票档（库存热点行）
 */
@TableName("tkt_tier")
@Data
@EqualsAndHashCode(callSuper = true)
public class TierDO extends BaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;
    /** 场次ID */
    private Long sessionId;
    /** 票档名称，如 VIP、看台A */
    private String tierName;
    /** 单价（分） */
    private Integer priceCent;
    /** 总库存张数 */
    private Integer totalStock;
    /** 已占用/已售张数（含待支付） */
    private Integer soldStock;
    /** 单人限购张数 */
    private Integer perUserLimit;
    /** 乐观锁版本 */
    @Version
    private Integer version;
    /**
     * 状态：1上架 0下架
     * 枚举 {@link TierStatusEnum}
     */
    private Integer status;
    /** 租户ID */
    private Long tenantId;
}
