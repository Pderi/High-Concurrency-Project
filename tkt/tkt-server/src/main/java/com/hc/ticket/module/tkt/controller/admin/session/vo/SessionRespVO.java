package com.hc.ticket.module.tkt.controller.admin.session.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理端 - 场次详情 Response VO")
@Data
public class SessionRespVO {

    @Schema(description = "主键", example = "1")
    private Long id;

    @Schema(description = "演出ID", example = "1")
    private Long showId;

    @Schema(description = "场馆名称")
    private String venueName;

    @Schema(description = "开场时间")
    private LocalDateTime startTime;

    @Schema(description = "开售时间")
    private LocalDateTime saleStartTime;

    @Schema(description = "停售时间")
    private LocalDateTime saleEndTime;

    @Schema(description = "场次状态")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
