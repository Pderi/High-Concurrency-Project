package com.hc.ticket.module.tkt.service.order;

import com.hc.ticket.framework.common.pojo.PageResult;
import com.hc.ticket.framework.common.util.object.BeanUtils;
import com.hc.ticket.module.tkt.controller.admin.order.vo.OrderPageReqVO;
import com.hc.ticket.module.tkt.controller.admin.order.vo.OrderPageRespVO;
import com.hc.ticket.module.tkt.controller.admin.order.vo.OrderRespVO;
import com.hc.ticket.module.tkt.dal.dataobject.order.OrderDO;
import com.hc.ticket.module.tkt.dal.mysql.order.OrderMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import static com.hc.ticket.framework.common.exception.ServiceExceptionUtil.exception;
import static com.hc.ticket.module.tkt.constants.ErrorCodeConstants.ORDER_NOT_EXISTS;

@Service
@Validated
public class OrderServiceImpl implements OrderService {

    @Resource
    private OrderMapper orderMapper;

    @Override
    public OrderRespVO getOrder(Long id) {
        OrderDO order = orderMapper.selectById(id);
        if (order == null) {
            throw exception(ORDER_NOT_EXISTS);
        }
        return BeanUtils.toBean(order, OrderRespVO.class);
    }

    @Override
    public PageResult<OrderPageRespVO> getOrderPage(OrderPageReqVO reqVO) {
        return BeanUtils.toBean(orderMapper.selectPage(reqVO), OrderPageRespVO.class);
    }
}
