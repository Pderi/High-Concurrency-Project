package com.hc.ticket.module.tkt.controller.admin.tier.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理端 - 票档详情 Response VO")
@Data
public class TierRespVO {

    @Schema(description = "主键", example = "1")
    private Long id;

    @Schema(description = "场次ID", example = "1")
    private Long sessionId;

    @Schema(description = "票档名称")
    private String tierName;

    @Schema(description = "单价（分）")
    private Integer priceCent;

    @Schema(description = "总库存张数")
    private Integer totalStock;

    @Schema(description = "已占用/已售张数（含待支付）")
    private Integer soldStock;

    @Schema(description = "单人限购张数")
    private Integer perUserLimit;

    @Schema(description = "乐观锁版本")
    private Integer version;

    @Schema(description = "状态：1上架 0下架")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
