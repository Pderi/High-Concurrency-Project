package com.hc.ticket.module.tkt.controller.admin.session.vo;

import com.hc.ticket.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理端 - 场次分页查询 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class SessionPageReqVO extends PageParam {

    @Schema(description = "演出ID", example = "1")
    private Long showId;

    @Schema(description = "场次状态", example = "2")
    private Integer status;
}
