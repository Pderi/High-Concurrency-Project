package com.hc.ticket.module.tkt.dal.mysql.order;

import com.hc.ticket.framework.common.pojo.PageResult;
import com.hc.ticket.framework.mybatis.core.mapper.BaseMapperX;
import com.hc.ticket.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hc.ticket.module.tkt.controller.admin.order.vo.OrderPageReqVO;
import com.hc.ticket.module.tkt.dal.dataobject.order.OrderDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper extends BaseMapperX<OrderDO> {

    default PageResult<OrderDO> selectPage(OrderPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<OrderDO>()
                .eqIfPresent(OrderDO::getOrderNo, reqVO.getOrderNo())
                .eqIfPresent(OrderDO::getUserId, reqVO.getUserId())
                .eqIfPresent(OrderDO::getSessionId, reqVO.getSessionId())
                .eqIfPresent(OrderDO::getTierId, reqVO.getTierId())
                .eqIfPresent(OrderDO::getOrderStatus, reqVO.getOrderStatus())
                .orderByDesc(OrderDO::getId));
    }

    default OrderDO selectByOrderNo(String orderNo) {
        return selectOne(new LambdaQueryWrapperX<OrderDO>()
                .eq(OrderDO::getOrderNo, orderNo));
    }
}
