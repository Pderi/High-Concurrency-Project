package com.hc.ticket.module.tkt.controller.app.order.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Schema(description = "用户端 - 抢票下单 Request VO")
@Data
public class AppOrderGrabReqVO {

    @Schema(description = "场次ID", requiredMode = REQUIRED, example = "1")
    @NotNull(message = "场次ID不能为空")
    private Long sessionId;

    @Schema(description = "票档ID", requiredMode = REQUIRED, example = "1")
    @NotNull(message = "票档ID不能为空")
    private Long tierId;

    @Schema(description = "购买张数", requiredMode = REQUIRED, example = "1")
    @NotNull(message = "购买张数不能为空")
    @Min(value = 1, message = "购买张数至少为 1")
    private Integer quantity;

    @Schema(description = "客户端幂等键", requiredMode = REQUIRED, example = "client-req-001")
    @NotBlank(message = "幂等键不能为空")
    private String idempotencyKey;
}
