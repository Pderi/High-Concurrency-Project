package com.hc.ticket.module.tkt.dal.mysql.session;

import com.hc.ticket.framework.common.pojo.PageResult;
import com.hc.ticket.framework.mybatis.core.mapper.BaseMapperX;
import com.hc.ticket.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hc.ticket.module.tkt.controller.admin.session.vo.SessionPageReqVO;
import com.hc.ticket.module.tkt.dal.dataobject.session.SessionDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SessionMapper extends BaseMapperX<SessionDO> {

    default PageResult<SessionDO> selectPage(SessionPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<SessionDO>()
                .eqIfPresent(SessionDO::getShowId, reqVO.getShowId())
                .eqIfPresent(SessionDO::getStatus, reqVO.getStatus())
                .orderByDesc(SessionDO::getId));
    }

    default List<SessionDO> selectListByShowId(Long showId) {
        return selectList(new LambdaQueryWrapperX<SessionDO>()
                .eqIfPresent(SessionDO::getShowId, showId)
                .orderByAsc(SessionDO::getStartTime));
    }
}
