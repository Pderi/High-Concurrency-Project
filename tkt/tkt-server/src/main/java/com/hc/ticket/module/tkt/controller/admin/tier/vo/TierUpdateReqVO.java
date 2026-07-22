package com.hc.ticket.module.tkt.controller.admin.tier.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Schema(description = "管理端 - 票档更新 Request VO")
@Data
public class TierUpdateReqVO {

    @Schema(description = "主键", requiredMode = REQUIRED, example = "1")
    @NotNull(message = "主键不能为空")
    private Long id;

    @Schema(description = "场次ID", requiredMode = REQUIRED, example = "1")
    @NotNull(message = "场次ID不能为空")
    private Long sessionId;

    @Schema(description = "票档名称", requiredMode = REQUIRED, example = "VIP")
    @NotBlank(message = "票档名称不能为空")
    private String tierName;

    @Schema(description = "单价（分）", requiredMode = REQUIRED, example = "128000")
    @NotNull(message = "单价不能为空")
    @Min(value = 1, message = "单价必须大于 0")
    private Integer priceCent;

    @Schema(description = "总库存张数", requiredMode = REQUIRED, example = "1000")
    @NotNull(message = "总库存不能为空")
    @Min(value = 1, message = "总库存必须大于 0")
    private Integer totalStock;

    @Schema(description = "单人限购张数", example = "4")
    @Min(value = 1, message = "单人限购至少为 1")
    private Integer perUserLimit;

    @Schema(description = "状态：1上架 0下架", requiredMode = REQUIRED, example = "1")
    @NotNull(message = "状态不能为空")
    private Integer status;
}
