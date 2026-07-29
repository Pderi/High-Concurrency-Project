package com.hc.ticket.module.tkt.service.ticket;

import com.hc.ticket.framework.common.pojo.PageResult;
import com.hc.ticket.module.tkt.controller.app.ticket.vo.AppTicketPageReqVO;
import com.hc.ticket.module.tkt.controller.app.ticket.vo.AppTicketPageRespVO;
import com.hc.ticket.module.tkt.dal.dataobject.order.OrderDO;

/**
 * 电子票领域服务
 */
public interface TicketService {

    /**
     * 按订单生成电子票（张数 = order.quantity）；若已存在则跳过
     */
    void createTicketsForOrder(OrderDO order);

    PageResult<AppTicketPageRespVO> getMyTicketPage(Long userId, AppTicketPageReqVO reqVO);
}
