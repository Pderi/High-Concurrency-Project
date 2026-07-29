package com.hc.ticket.module.tkt.controller.app.order.vo;

import com.hc.ticket.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "用户端 - 我的订单分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class AppOrderPageReqVO extends PageParam {

    @Schema(description = "订单状态（可选）", example = "10")
    private Integer orderStatus;
}
