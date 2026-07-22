package com.hc.ticket.module.tkt.service.order;

import com.hc.ticket.framework.common.pojo.PageResult;
import com.hc.ticket.module.tkt.controller.admin.order.vo.OrderPageReqVO;
import com.hc.ticket.module.tkt.controller.admin.order.vo.OrderPageRespVO;
import com.hc.ticket.module.tkt.controller.admin.order.vo.OrderRespVO;

/**
 * 订单领域服务（管理端只读）
 */
public interface OrderService {

    OrderRespVO getOrder(Long id);

    PageResult<OrderPageRespVO> getOrderPage(OrderPageReqVO reqVO);
}
