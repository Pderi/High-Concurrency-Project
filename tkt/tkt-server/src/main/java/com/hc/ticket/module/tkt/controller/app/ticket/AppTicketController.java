package com.hc.ticket.module.tkt.controller.app.ticket;

import com.hc.ticket.framework.common.pojo.CommonResult;
import com.hc.ticket.framework.common.pojo.PageResult;
import com.hc.ticket.module.tkt.constants.ApiConstants;
import com.hc.ticket.module.tkt.controller.app.ticket.vo.AppTicketPageReqVO;
import com.hc.ticket.module.tkt.controller.app.ticket.vo.AppTicketPageRespVO;
import com.hc.ticket.module.tkt.service.ticket.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.hc.ticket.framework.common.pojo.CommonResult.success;

@Tag(name = "用户端 - 电子票")
@RestController
@RequestMapping(ApiConstants.APP_API_PREFIX + "/ticket")
@Validated
public class AppTicketController {

    @Resource
    private TicketService ticketService;

    @GetMapping("/page")
    @Operation(summary = "我的电子票分页")
    public CommonResult<PageResult<AppTicketPageRespVO>> getMyTicketPage(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @Valid AppTicketPageReqVO reqVO) {
        return success(ticketService.getMyTicketPage(userId, reqVO));
    }
}
