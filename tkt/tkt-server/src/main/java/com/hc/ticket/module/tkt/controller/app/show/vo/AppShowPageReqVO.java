package com.hc.ticket.module.tkt.controller.app.show.vo;

import com.hc.ticket.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "用户端 - 演出分页查询 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class AppShowPageReqVO extends PageParam {

    @Schema(description = "演出名称（模糊）", example = "星河")
    private String name;
}
