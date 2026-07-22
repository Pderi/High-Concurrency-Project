package com.hc.ticket.module.tkt.controller.admin.order.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理端 - 订单分页 Response VO")
@Data
public class OrderPageRespVO {

    @Schema(description = "主键", example = "1")
    private Long id;

    @Schema(description = "业务订单号")
    private String orderNo;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "场次ID")
    private Long sessionId;

    @Schema(description = "票档ID")
    private Long tierId;

    @Schema(description = "购买张数")
    private Integer quantity;

    @Schema(description = "应付总金额（分）")
    private Integer totalAmountCent;

    @Schema(description = "订单状态")
    private Integer orderStatus;

    @Schema(description = "支付渠道")
    private Integer payChannel;

    @Schema(description = "支付截止时间")
    private LocalDateTime payDeadline;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
