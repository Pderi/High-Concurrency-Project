package com.hc.ticket.module.tkt.controller.admin.show.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Schema(description = "管理端 - 演出更新 Request VO")
@Data
public class ShowUpdateReqVO {

    @Schema(description = "主键", requiredMode = REQUIRED, example = "1")
    @NotNull(message = "主键不能为空")
    private Long id;

    @Schema(description = "演出名称", requiredMode = REQUIRED, example = "周杰伦演唱会")
    @NotBlank(message = "演出名称不能为空")
    private String name;

    @Schema(description = "副标题", example = "嘉年华世界巡回")
    private String subtitle;

    @Schema(description = "封面图 URL", example = "https://cdn.example.com/cover.jpg")
    private String coverUrl;

    @Schema(description = "详情（富文本）")
    private String description;

    @Schema(description = "状态：0草稿 1已发布 2已下架", requiredMode = REQUIRED, example = "1")
    @NotNull(message = "状态不能为空")
    private Integer status;

    @Schema(description = "列表排序，越大越靠前", example = "10")
    private Integer sort;
}
