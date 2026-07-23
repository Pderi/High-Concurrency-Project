package com.hc.ticket.module.tkt.controller.app.order.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "用户端 - 抢票结果轮询 Response VO")
@Data
public class AppOrderGrabResultRespVO {

    @Schema(description = "0处理中 1成功 2失败")
    private Integer status;

    @Schema(description = "订单号（成功时有值）")
    private String orderNo;

    @Schema(description = "失败错误码")
    private Integer errorCode;

    @Schema(description = "失败错误信息")
    private String errorMsg;
}
