package com.hc.ticket.module.tkt.controller.admin.session;

import com.hc.ticket.framework.common.pojo.CommonResult;
import com.hc.ticket.framework.common.pojo.PageResult;
import com.hc.ticket.module.tkt.constants.ApiConstants;
import com.hc.ticket.module.tkt.controller.admin.session.vo.SessionAddReqVO;
import com.hc.ticket.module.tkt.controller.admin.session.vo.SessionPageReqVO;
import com.hc.ticket.module.tkt.controller.admin.session.vo.SessionPageRespVO;
import com.hc.ticket.module.tkt.controller.admin.session.vo.SessionRespVO;
import com.hc.ticket.module.tkt.controller.admin.session.vo.SessionUpdateReqVO;
import com.hc.ticket.module.tkt.service.session.SessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.hc.ticket.framework.common.pojo.CommonResult.success;

@Tag(name = "管理端 - 场次")
@RestController
@RequestMapping(ApiConstants.ADMIN_API_PREFIX + "/session")
@Validated
public class SessionController {

    @Resource
    private SessionService sessionService;

    @PostMapping("/create")
    @Operation(summary = "新增场次")
    public CommonResult<Long> createSession(@Valid @RequestBody SessionAddReqVO reqVO) {
        return success(sessionService.createSession(reqVO));
    }

    @PostMapping("/update")
    @Operation(summary = "修改场次")
    public CommonResult<Boolean> updateSession(@Valid @RequestBody SessionUpdateReqVO reqVO) {
        sessionService.updateSession(reqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除场次")
    public CommonResult<Boolean> deleteSession(@RequestParam("id") Long id) {
        sessionService.deleteSession(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获取场次详情")
    public CommonResult<SessionRespVO> getSession(@RequestParam("id") Long id) {
        return success(sessionService.getSession(id));
    }

    @GetMapping("/page")
    @Operation(summary = "获取场次分页")
    public CommonResult<PageResult<SessionPageRespVO>> getSessionPage(@Valid SessionPageReqVO reqVO) {
        return success(sessionService.getSessionPage(reqVO));
    }
}
