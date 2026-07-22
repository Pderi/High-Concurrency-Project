package com.hc.ticket.module.tkt.controller.admin.show;

import com.hc.ticket.framework.common.pojo.CommonResult;
import com.hc.ticket.framework.common.pojo.PageResult;
import com.hc.ticket.module.tkt.constants.ApiConstants;
import com.hc.ticket.module.tkt.controller.admin.show.vo.ShowAddReqVO;
import com.hc.ticket.module.tkt.controller.admin.show.vo.ShowPageReqVO;
import com.hc.ticket.module.tkt.controller.admin.show.vo.ShowPageRespVO;
import com.hc.ticket.module.tkt.controller.admin.show.vo.ShowRespVO;
import com.hc.ticket.module.tkt.controller.admin.show.vo.ShowUpdateReqVO;
import com.hc.ticket.module.tkt.service.show.ShowService;
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

@Tag(name = "管理端 - 演出")
@RestController
@RequestMapping(ApiConstants.ADMIN_API_PREFIX + "/show")
@Validated
public class ShowController {

    @Resource
    private ShowService showService;

    @PostMapping("/create")
    @Operation(summary = "新增演出")
    public CommonResult<Long> createShow(@Valid @RequestBody ShowAddReqVO reqVO) {
        return success(showService.createShow(reqVO));
    }

    @PostMapping("/update")
    @Operation(summary = "修改演出")
    public CommonResult<Boolean> updateShow(@Valid @RequestBody ShowUpdateReqVO reqVO) {
        showService.updateShow(reqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除演出")
    public CommonResult<Boolean> deleteShow(@RequestParam("id") Long id) {
        showService.deleteShow(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获取演出详情")
    public CommonResult<ShowRespVO> getShow(@RequestParam("id") Long id) {
        return success(showService.getShow(id));
    }

    @GetMapping("/page")
    @Operation(summary = "获取演出分页")
    public CommonResult<PageResult<ShowPageRespVO>> getShowPage(@Valid ShowPageReqVO reqVO) {
        return success(showService.getShowPage(reqVO));
    }
}
