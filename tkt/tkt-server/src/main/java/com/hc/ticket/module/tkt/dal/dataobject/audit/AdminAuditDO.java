package com.hc.ticket.module.tkt.dal.dataobject.audit;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 管理端审计（无逻辑删除等公共字段，不继承 BaseDO）
 */
@TableName("tkt_admin_audit")
@Data
public class AdminAuditDO implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;
    /** 管理员用户ID */
    private Long adminUserId;
    /** 动作，如 SHOW_PUBLISH */
    private String action;
    /** 业务类型 show/session/tier */
    private String bizType;
    /** 业务主键 */
    private Long bizId;
    /** 变更快照 JSON */
    private String payloadJson;
    private LocalDateTime createTime;
    /** 租户ID */
    private Long tenantId;
}
