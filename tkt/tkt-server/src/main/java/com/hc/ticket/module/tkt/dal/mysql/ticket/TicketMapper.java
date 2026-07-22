package com.hc.ticket.module.tkt.dal.mysql.ticket;

import com.hc.ticket.framework.mybatis.core.mapper.BaseMapperX;
import com.hc.ticket.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hc.ticket.module.tkt.dal.dataobject.ticket.TicketDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TicketMapper extends BaseMapperX<TicketDO> {

    default List<TicketDO> selectListByOrderId(Long orderId) {
        return selectList(new LambdaQueryWrapperX<TicketDO>()
                .eq(TicketDO::getOrderId, orderId)
                .orderByAsc(TicketDO::getId));
    }
}
