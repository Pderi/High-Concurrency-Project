package com.hc.ticket.module.tkt.controller.app.session.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "用户端 - 场次下票档项")
@Data
public class AppSessionTierRespVO {

    @Schema(description = "票档ID", example = "1")
    private Long id;

    @Schema(description = "票档名称", example = "VIP")
    private String tierName;

    @Schema(description = "单价（分）", example = "128000")
    private Integer priceCent;

    @Schema(description = "余票参考值", example = "100")
    private Integer remainStock;

    @Schema(description = "单人限购", example = "2")
    private Integer perUserLimit;
}
