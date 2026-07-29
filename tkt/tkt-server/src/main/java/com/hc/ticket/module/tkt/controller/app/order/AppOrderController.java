package com.hc.ticket.module.tkt.controller.app.order;

import com.hc.ticket.framework.common.pojo.CommonResult;
import com.hc.ticket.framework.common.pojo.PageResult;
import com.hc.ticket.module.tkt.constants.ApiConstants;
import com.hc.ticket.module.tkt.controller.app.order.vo.AppOrderGrabReqVO;
import com.hc.ticket.module.tkt.controller.app.order.vo.AppOrderGrabRespVO;
import com.hc.ticket.module.tkt.controller.app.order.vo.AppOrderGrabResultRespVO;
import com.hc.ticket.module.tkt.controller.app.order.vo.AppOrderPageReqVO;
import com.hc.ticket.module.tkt.controller.app.order.vo.AppOrderPageRespVO;
import com.hc.ticket.module.tkt.controller.app.order.vo.AppOrderRespVO;
import com.hc.ticket.module.tkt.service.order.OrderGrabService;
import com.hc.ticket.module.tkt.service.order.OrderPayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.hc.ticket.framework.common.exception.ServiceExceptionUtil.exception;
import static com.hc.ticket.framework.common.pojo.CommonResult.success;
import static com.hc.ticket.module.tkt.constants.ErrorCodeConstants.USER_ID_REQUIRED;

@Tag(name = "用户端 - 订单抢票")
@RestController
@RequestMapping(ApiConstants.APP_API_PREFIX + "/order")
@Validated
public class AppOrderController {

    @Resource
    private OrderGrabService orderGrabService;
    @Resource
    private OrderPayService orderPayService;

    @PostMapping("/grab")
    @Operation(summary = "抢票下单（受理 + 轮询）")
    public CommonResult<AppOrderGrabRespVO> grab(
            @Parameter(description = "用户ID（首期 Header mock）")
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @Valid @RequestBody AppOrderGrabReqVO reqVO) {
        if (userId == null) {
            throw exception(USER_ID_REQUIRED);
        }
        return success(orderGrabService.grab(userId, reqVO));
    }

    @GetMapping("/grab-result")
    @Operation(summary = "轮询抢票受理结果")
    public CommonResult<AppOrderGrabResultRespVO> grabResult(@RequestParam("acceptToken") String acceptToken) {
        return success(orderGrabService.getGrabResult(acceptToken));
    }

    @GetMapping("/get")
    @Operation(summary = "查询我的订单详情")
    public CommonResult<AppOrderRespVO> getMyOrder(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestParam("orderNo") String orderNo) {
        return success(orderGrabService.getMyOrder(userId, orderNo));
    }

    @GetMapping("/page")
    @Operation(summary = "我的订单分页")
    public CommonResult<PageResult<AppOrderPageRespVO>> getMyOrderPage(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @Valid AppOrderPageReqVO reqVO) {
        return success(orderPayService.getMyOrderPage(userId, reqVO));
    }

    @PostMapping("/{orderNo}/pay")
    @Operation(summary = "模拟支付")
    public CommonResult<AppOrderRespVO> pay(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @PathVariable("orderNo") String orderNo) {
        return success(orderPayService.pay(userId, orderNo));
    }
}
