package com.hc.ticket.module.tkt.controller.app;

import com.hc.ticket.framework.common.pojo.CommonResult;
import com.hc.ticket.module.tkt.constants.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "用户端 - 探活")
@RestController
@RequestMapping(ApiConstants.APP_API_PREFIX + "/ping")
public class AppPingController {

    @GetMapping
    @Operation(summary = "探活")
    public CommonResult<String> ping() {
        return CommonResult.success("pong");
    }
}
