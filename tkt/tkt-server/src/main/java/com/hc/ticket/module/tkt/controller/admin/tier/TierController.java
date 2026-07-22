package com.hc.ticket.module.tkt.controller.admin.tier;

import com.hc.ticket.framework.common.pojo.CommonResult;
import com.hc.ticket.framework.common.pojo.PageResult;
import com.hc.ticket.module.tkt.constants.ApiConstants;
import com.hc.ticket.module.tkt.controller.admin.tier.vo.TierAddReqVO;
import com.hc.ticket.module.tkt.controller.admin.tier.vo.TierPageReqVO;
import com.hc.ticket.module.tkt.controller.admin.tier.vo.TierPageRespVO;
import com.hc.ticket.module.tkt.controller.admin.tier.vo.TierRespVO;
import com.hc.ticket.module.tkt.controller.admin.tier.vo.TierUpdateReqVO;
import com.hc.ticket.module.tkt.service.tier.TierService;
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

@Tag(name = "管理端 - 票档")
@RestController
@RequestMapping(ApiConstants.ADMIN_API_PREFIX + "/tier")
@Validated
public class TierController {

    @Resource
    private TierService tierService;

    @PostMapping("/create")
    @Operation(summary = "新增票档")
    public CommonResult<Long> createTier(@Valid @RequestBody TierAddReqVO reqVO) {
        return success(tierService.createTier(reqVO));
    }

    @PostMapping("/update")
    @Operation(summary = "修改票档")
    public CommonResult<Boolean> updateTier(@Valid @RequestBody TierUpdateReqVO reqVO) {
        tierService.updateTier(reqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除票档")
    public CommonResult<Boolean> deleteTier(@RequestParam("id") Long id) {
        tierService.deleteTier(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获取票档详情")
    public CommonResult<TierRespVO> getTier(@RequestParam("id") Long id) {
        return success(tierService.getTier(id));
    }

    @GetMapping("/page")
    @Operation(summary = "获取票档分页")
    public CommonResult<PageResult<TierPageRespVO>> getTierPage(@Valid TierPageReqVO reqVO) {
        return success(tierService.getTierPage(reqVO));
    }
}
