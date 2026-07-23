package com.hc.ticket.module.tkt.controller.app.session;

import com.hc.ticket.framework.common.pojo.CommonResult;
import com.hc.ticket.module.tkt.constants.ApiConstants;
import com.hc.ticket.module.tkt.controller.app.session.vo.AppSessionDetailRespVO;
import com.hc.ticket.module.tkt.service.session.SessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.hc.ticket.framework.common.pojo.CommonResult.success;

@Tag(name = "用户端 - 场次")
@RestController
@RequestMapping(ApiConstants.APP_API_PREFIX + "/session")
@Validated
public class AppSessionController {

    @Resource
    private SessionService sessionService;

    @GetMapping("/get")
    @Operation(summary = "场次详情（含上架票档与余票）")
    public CommonResult<AppSessionDetailRespVO> getSession(@RequestParam("id") Long id) {
        return success(sessionService.getAppSessionDetail(id));
    }
}
