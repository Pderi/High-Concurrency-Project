package com.hc.ticket.module.tkt.controller.app.show;

import com.hc.ticket.framework.common.pojo.CommonResult;
import com.hc.ticket.framework.common.pojo.PageResult;
import com.hc.ticket.module.tkt.constants.ApiConstants;
import com.hc.ticket.module.tkt.controller.app.show.vo.AppShowPageReqVO;
import com.hc.ticket.module.tkt.controller.app.show.vo.AppShowPageRespVO;
import com.hc.ticket.module.tkt.controller.app.show.vo.AppShowRespVO;
import com.hc.ticket.module.tkt.service.show.ShowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.hc.ticket.framework.common.pojo.CommonResult.success;

@Tag(name = "用户端 - 演出")
@RestController
@RequestMapping(ApiConstants.APP_API_PREFIX + "/show")
@Validated
public class AppShowController {

    @Resource
    private ShowService showService;

    @GetMapping("/page")
    @Operation(summary = "已发布演出分页")
    public CommonResult<PageResult<AppShowPageRespVO>> getShowPage(@Valid AppShowPageReqVO reqVO) {
        return success(showService.getAppShowPage(reqVO));
    }

    @GetMapping("/get")
    @Operation(summary = "已发布演出详情")
    public CommonResult<AppShowRespVO> getShow(@RequestParam("id") Long id) {
        return success(showService.getAppShow(id));
    }
}
