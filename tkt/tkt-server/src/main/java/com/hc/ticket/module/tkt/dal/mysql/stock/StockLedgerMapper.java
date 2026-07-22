package com.hc.ticket.module.tkt.dal.mysql.stock;

import com.hc.ticket.framework.mybatis.core.mapper.BaseMapperX;
import com.hc.ticket.module.tkt.dal.dataobject.stock.StockLedgerDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StockLedgerMapper extends BaseMapperX<StockLedgerDO> {
}
