package com.hc.ticket.module.tkt.controller.app.order.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "用户端 - 订单详情 Response VO")
@Data
public class AppOrderRespVO {

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "场次ID")
    private Long sessionId;

    @Schema(description = "票档ID")
    private Long tierId;

    @Schema(description = "购买张数")
    private Integer quantity;

    @Schema(description = "单价（分）")
    private Integer unitPriceCent;

    @Schema(description = "应付总金额（分）")
    private Integer totalAmountCent;

    @Schema(description = "订单状态")
    private Integer orderStatus;

    @Schema(description = "支付截止时间")
    private LocalDateTime payDeadline;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
