package com.hc.ticket.module.tkt.dal.mysql.tier;

import com.hc.ticket.framework.common.pojo.PageResult;
import com.hc.ticket.framework.mybatis.core.mapper.BaseMapperX;
import com.hc.ticket.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hc.ticket.module.tkt.controller.admin.tier.vo.TierPageReqVO;
import com.hc.ticket.module.tkt.dal.dataobject.tier.TierDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TierMapper extends BaseMapperX<TierDO> {

    default PageResult<TierDO> selectPage(TierPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<TierDO>()
                .eqIfPresent(TierDO::getSessionId, reqVO.getSessionId())
                .eqIfPresent(TierDO::getStatus, reqVO.getStatus())
                .orderByDesc(TierDO::getId));
    }

    default List<TierDO> selectListBySessionId(Long sessionId) {
        return selectList(new LambdaQueryWrapperX<TierDO>()
                .eqIfPresent(TierDO::getSessionId, sessionId)
                .orderByAsc(TierDO::getId));
    }
}
