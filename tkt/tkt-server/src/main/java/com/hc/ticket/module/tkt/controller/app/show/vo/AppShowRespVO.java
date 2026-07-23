package com.hc.ticket.module.tkt.controller.app.show.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "用户端 - 演出详情 Response VO")
@Data
public class AppShowRespVO {

    @Schema(description = "主键", example = "1")
    private Long id;

    @Schema(description = "演出名称")
    private String name;

    @Schema(description = "副标题")
    private String subtitle;

    @Schema(description = "封面图 URL")
    private String coverUrl;

    @Schema(description = "详情（富文本）")
    private String description;
}
