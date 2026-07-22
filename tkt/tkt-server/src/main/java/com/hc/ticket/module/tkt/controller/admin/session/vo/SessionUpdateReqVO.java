package com.hc.ticket.module.tkt.controller.admin.session.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Schema(description = "管理端 - 场次更新 Request VO")
@Data
public class SessionUpdateReqVO {

    @Schema(description = "主键", requiredMode = REQUIRED, example = "1")
    @NotNull(message = "主键不能为空")
    private Long id;

    @Schema(description = "演出ID", requiredMode = REQUIRED, example = "1")
    @NotNull(message = "演出ID不能为空")
    private Long showId;

    @Schema(description = "场馆名称", requiredMode = REQUIRED, example = "国家体育场")
    @NotBlank(message = "场馆名称不能为空")
    private String venueName;

    @Schema(description = "开场时间", requiredMode = REQUIRED)
    @NotNull(message = "开场时间不能为空")
    private LocalDateTime startTime;

    @Schema(description = "开售时间", requiredMode = REQUIRED)
    @NotNull(message = "开售时间不能为空")
    private LocalDateTime saleStartTime;

    @Schema(description = "停售时间，可空")
    private LocalDateTime saleEndTime;

    @Schema(description = "场次状态：0草稿 1即将开售 2开售中 3停售 4结束", requiredMode = REQUIRED, example = "2")
    @NotNull(message = "状态不能为空")
    private Integer status;
}
