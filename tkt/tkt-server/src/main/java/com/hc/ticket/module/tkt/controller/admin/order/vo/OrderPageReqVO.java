package com.hc.ticket.module.tkt.controller.admin.order.vo;

import com.hc.ticket.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理端 - 订单分页查询 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class OrderPageReqVO extends PageParam {

    @Schema(description = "业务订单号", example = "TKT202607220001")
    private String orderNo;

    @Schema(description = "用户ID", example = "10001")
    private Long userId;

    @Schema(description = "场次ID", example = "1")
    private Long sessionId;

    @Schema(description = "票档ID", example = "1")
    private Long tierId;

    @Schema(description = "订单状态：10待支付 20已支付 30已关闭 40已退款", example = "10")
    private Integer orderStatus;
}
