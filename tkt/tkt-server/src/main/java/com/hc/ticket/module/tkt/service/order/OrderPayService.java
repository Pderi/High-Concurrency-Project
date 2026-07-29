package com.hc.ticket.module.tkt.service.order;

import com.hc.ticket.framework.common.pojo.PageResult;
import com.hc.ticket.module.tkt.controller.app.order.vo.AppOrderPageReqVO;
import com.hc.ticket.module.tkt.controller.app.order.vo.AppOrderPageRespVO;
import com.hc.ticket.module.tkt.controller.app.order.vo.AppOrderRespVO;

/**
 * C 端模拟支付与订单查询
 */
public interface OrderPayService {

    AppOrderRespVO pay(Long userId, String orderNo);

    PageResult<AppOrderPageRespVO> getMyOrderPage(Long userId, AppOrderPageReqVO reqVO);
}
