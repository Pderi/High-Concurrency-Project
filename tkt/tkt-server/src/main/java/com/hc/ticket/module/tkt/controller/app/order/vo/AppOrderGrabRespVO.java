package com.hc.ticket.module.tkt.controller.app.order.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "用户端 - 抢票受理 Response VO")
@Data
public class AppOrderGrabRespVO {

    @Schema(description = "受理令牌，用于轮询结果")
    private String acceptToken;

    @Schema(description = "建议轮询间隔（秒）")
    private Integer pollSeconds;

    @Schema(description = "若同步建单成功，可直接返回订单号；异步时为空")
    private String orderNo;
}
