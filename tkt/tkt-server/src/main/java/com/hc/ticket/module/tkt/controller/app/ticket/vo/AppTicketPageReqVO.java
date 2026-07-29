package com.hc.ticket.module.tkt.controller.app.ticket.vo;

import com.hc.ticket.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "用户端 - 我的电子票分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class AppTicketPageReqVO extends PageParam {

    @Schema(description = "票状态（可选）", example = "1")
    private Integer ticketStatus;
}
