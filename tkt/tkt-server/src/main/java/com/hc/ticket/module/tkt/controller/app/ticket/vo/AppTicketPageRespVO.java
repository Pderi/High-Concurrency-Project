package com.hc.ticket.module.tkt.controller.app.ticket.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "用户端 - 我的电子票分页 Response VO")
@Data
public class AppTicketPageRespVO {

    @Schema(description = "票号")
    private String ticketNo;

    @Schema(description = "订单ID")
    private Long orderId;

    @Schema(description = "场次ID")
    private Long sessionId;

    @Schema(description = "票档ID")
    private Long tierId;

    @Schema(description = "票状态")
    private Integer ticketStatus;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
