package com.hc.ticket.module.tkt.controller.admin.show.vo;

import com.hc.ticket.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理端 - 演出分页查询 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class ShowPageReqVO extends PageParam {

    @Schema(description = "演出名称（模糊）", example = "周杰伦")
    private String name;

    @Schema(description = "状态：0草稿 1已发布 2已下架", example = "1")
    private Integer status;
}
