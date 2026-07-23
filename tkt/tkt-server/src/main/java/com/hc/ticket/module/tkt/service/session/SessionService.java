package com.hc.ticket.module.tkt.service.session;

import com.hc.ticket.framework.common.pojo.PageResult;
import com.hc.ticket.module.tkt.controller.admin.session.vo.SessionAddReqVO;
import com.hc.ticket.module.tkt.controller.admin.session.vo.SessionPageReqVO;
import com.hc.ticket.module.tkt.controller.admin.session.vo.SessionPageRespVO;
import com.hc.ticket.module.tkt.controller.admin.session.vo.SessionRespVO;
import com.hc.ticket.module.tkt.controller.admin.session.vo.SessionUpdateReqVO;
import com.hc.ticket.module.tkt.controller.app.session.vo.AppSessionDetailRespVO;
import com.hc.ticket.module.tkt.dal.dataobject.session.SessionDO;

/**
 * 场次领域服务
 */
public interface SessionService {

    Long createSession(SessionAddReqVO reqVO);

    void updateSession(SessionUpdateReqVO reqVO);

    void deleteSession(Long id);

    SessionRespVO getSession(Long id);

    PageResult<SessionPageRespVO> getSessionPage(SessionPageReqVO reqVO);

    /** C 端：场次详情 + 上架票档余票（场次/演出走 Redis 元数据） */
    AppSessionDetailRespVO getAppSessionDetail(Long id);

    /** 票档变更后刷新场次元数据（Redis SET） */
    void clearAppSessionDetailCache(Long sessionId);

    SessionDO validateSessionExists(Long id);
}
