package com.hc.ticket.module.tkt.dal.dataobject.session;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hc.ticket.framework.mybatis.core.dataobject.BaseDO;
import com.hc.ticket.module.tkt.enums.SessionStatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 场次
 */
@TableName("tkt_session")
@Data
@EqualsAndHashCode(callSuper = true)
public class SessionDO extends BaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;
    /** 演出ID */
    private Long showId;
    /** 场馆名称 */
    private String venueName;
    /** 开场时间 */
    private LocalDateTime startTime;
    /** 开售时间 */
    private LocalDateTime saleStartTime;
    /** 停售时间，NULL 表示无单独停售时间 */
    private LocalDateTime saleEndTime;
    /**
     * 场次状态
     * 枚举 {@link SessionStatusEnum}
     */
    private Integer status;
    /** 租户ID */
    private Long tenantId;
}
