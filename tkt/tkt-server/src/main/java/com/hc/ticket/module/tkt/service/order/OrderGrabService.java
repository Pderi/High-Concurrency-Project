package com.hc.ticket.module.tkt.service.order;

import com.hc.ticket.module.tkt.controller.app.order.vo.AppOrderGrabReqVO;
import com.hc.ticket.module.tkt.controller.app.order.vo.AppOrderGrabRespVO;
import com.hc.ticket.module.tkt.controller.app.order.vo.AppOrderGrabResultRespVO;
import com.hc.ticket.module.tkt.controller.app.order.vo.AppOrderRespVO;

/**
 * C 端抢票与订单查询
 */
public interface OrderGrabService {

    AppOrderGrabRespVO grab(Long userId, AppOrderGrabReqVO reqVO);

    AppOrderGrabResultRespVO getGrabResult(String acceptToken);

    AppOrderRespVO getMyOrder(Long userId, String orderNo);
}
