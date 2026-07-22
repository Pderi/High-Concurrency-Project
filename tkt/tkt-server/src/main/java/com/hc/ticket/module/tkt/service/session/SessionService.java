package com.hc.ticket.module.tkt.service.session;

import com.hc.ticket.framework.common.pojo.PageResult;
import com.hc.ticket.module.tkt.controller.admin.session.vo.SessionAddReqVO;
import com.hc.ticket.module.tkt.controller.admin.session.vo.SessionPageReqVO;
import com.hc.ticket.module.tkt.controller.admin.session.vo.SessionPageRespVO;
import com.hc.ticket.module.tkt.controller.admin.session.vo.SessionRespVO;
import com.hc.ticket.module.tkt.controller.admin.session.vo.SessionUpdateReqVO;
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

    SessionDO validateSessionExists(Long id);
}
