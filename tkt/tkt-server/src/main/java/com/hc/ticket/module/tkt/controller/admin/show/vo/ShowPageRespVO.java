package com.hc.ticket.module.tkt.controller.admin.show.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理端 - 演出分页 Response VO")
@Data
public class ShowPageRespVO {

    @Schema(description = "主键", example = "1")
    private Long id;

    @Schema(description = "演出名称", example = "周杰伦演唱会")
    private String name;

    @Schema(description = "副标题")
    private String subtitle;

    @Schema(description = "封面图 URL")
    private String coverUrl;

    @Schema(description = "状态：0草稿 1已发布 2已下架", example = "1")
    private Integer status;

    @Schema(description = "列表排序", example = "10")
    private Integer sort;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
