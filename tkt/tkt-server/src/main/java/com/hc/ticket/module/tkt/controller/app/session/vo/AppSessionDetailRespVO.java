package com.hc.ticket.module.tkt.controller.app.session.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "用户端 - 场次详情（含票档）Response VO")
@Data
public class AppSessionDetailRespVO {

    @Schema(description = "场次ID", example = "1")
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

    @Schema(description = "上架票档列表")
    private List<AppSessionTierRespVO> tiers;
}
