package com.hc.ticket.module.tkt.dal.mysql.audit;

import com.hc.ticket.framework.mybatis.core.mapper.BaseMapperX;
import com.hc.ticket.module.tkt.dal.dataobject.audit.AdminAuditDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AdminAuditMapper extends BaseMapperX<AdminAuditDO> {
}
