package com.hc.ticket.module.tkt.controller.admin.tier.vo;

import com.hc.ticket.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理端 - 票档分页查询 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class TierPageReqVO extends PageParam {

    @Schema(description = "场次ID", example = "1")
    private Long sessionId;

    @Schema(description = "状态：1上架 0下架", example = "1")
    private Integer status;
}
