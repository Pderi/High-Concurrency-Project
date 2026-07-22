package com.hc.ticket.module.tkt.controller.admin.order;

import com.hc.ticket.framework.common.pojo.CommonResult;
import com.hc.ticket.framework.common.pojo.PageResult;
import com.hc.ticket.module.tkt.constants.ApiConstants;
import com.hc.ticket.module.tkt.controller.admin.order.vo.OrderPageReqVO;
import com.hc.ticket.module.tkt.controller.admin.order.vo.OrderPageRespVO;
import com.hc.ticket.module.tkt.controller.admin.order.vo.OrderRespVO;
import com.hc.ticket.module.tkt.service.order.OrderService;
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

@Tag(name = "管理端 - 订单")
@RestController
@RequestMapping(ApiConstants.ADMIN_API_PREFIX + "/order")
@Validated
public class OrderController {

    @Resource
    private OrderService orderService;

    @GetMapping("/get")
    @Operation(summary = "获取订单详情")
    public CommonResult<OrderRespVO> getOrder(@RequestParam("id") Long id) {
        return success(orderService.getOrder(id));
    }

    @GetMapping("/page")
    @Operation(summary = "获取订单分页")
    public CommonResult<PageResult<OrderPageRespVO>> getOrderPage(@Valid OrderPageReqVO reqVO) {
        return success(orderService.getOrderPage(reqVO));
    }
}
