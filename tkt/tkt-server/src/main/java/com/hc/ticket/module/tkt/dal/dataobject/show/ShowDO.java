package com.hc.ticket.module.tkt.dal.dataobject.show;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hc.ticket.framework.mybatis.core.dataobject.BaseDO;
import com.hc.ticket.module.tkt.enums.ShowStatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 演出
 */
@TableName("tkt_show")
@Data
@EqualsAndHashCode(callSuper = true)
public class ShowDO extends BaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;
    /** 演出名称 */
    private String name;
    /** 副标题 */
    private String subtitle;
    /** 封面图 URL */
    private String coverUrl;
    /** 详情（富文本） */
    private String description;
    /**
     * 状态
     * 枚举 {@link ShowStatusEnum}
     */
    private Integer status;
    /** 列表排序，越大越靠前 */
    private Integer sort;
    /** 租户ID */
    private Long tenantId;
}
