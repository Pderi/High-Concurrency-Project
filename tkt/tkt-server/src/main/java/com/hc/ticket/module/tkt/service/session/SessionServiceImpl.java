package com.hc.ticket.module.tkt.service.session;

import com.hc.ticket.framework.common.pojo.PageResult;
import com.hc.ticket.framework.common.util.object.BeanUtils;
import com.hc.ticket.module.tkt.controller.admin.session.vo.SessionAddReqVO;
import com.hc.ticket.module.tkt.controller.admin.session.vo.SessionPageReqVO;
import com.hc.ticket.module.tkt.controller.admin.session.vo.SessionPageRespVO;
import com.hc.ticket.module.tkt.controller.admin.session.vo.SessionRespVO;
import com.hc.ticket.module.tkt.controller.admin.session.vo.SessionUpdateReqVO;
import com.hc.ticket.module.tkt.dal.dataobject.session.SessionDO;
import com.hc.ticket.module.tkt.dal.mysql.session.SessionMapper;
import com.hc.ticket.module.tkt.service.show.ShowService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import static com.hc.ticket.framework.common.exception.ServiceExceptionUtil.exception;
import static com.hc.ticket.module.tkt.constants.ErrorCodeConstants.SESSION_NOT_EXISTS;

@Service
@Validated
public class SessionServiceImpl implements SessionService {

    @Resource
    private SessionMapper sessionMapper;
    @Resource
    private ShowService showService;

    @Override
    public Long createSession(SessionAddReqVO reqVO) {
        showService.validateShowExists(reqVO.getShowId());
        SessionDO session = BeanUtils.toBean(reqVO, SessionDO.class);
        if (session.getTenantId() == null) {
            session.setTenantId(0L);
        }
        sessionMapper.insert(session);
        return session.getId();
    }

    @Override
    public void updateSession(SessionUpdateReqVO reqVO) {
        validateSessionExists(reqVO.getId());
        showService.validateShowExists(reqVO.getShowId());
        SessionDO updateObj = BeanUtils.toBean(reqVO, SessionDO.class);
        sessionMapper.updateById(updateObj);
    }

    @Override
    public void deleteSession(Long id) {
        validateSessionExists(id);
        sessionMapper.deleteById(id);
    }

    @Override
    public SessionRespVO getSession(Long id) {
        return BeanUtils.toBean(validateSessionExists(id), SessionRespVO.class);
    }

    @Override
    public PageResult<SessionPageRespVO> getSessionPage(SessionPageReqVO reqVO) {
        return BeanUtils.toBean(sessionMapper.selectPage(reqVO), SessionPageRespVO.class);
    }

    @Override
    public SessionDO validateSessionExists(Long id) {
        SessionDO session = sessionMapper.selectById(id);
        if (session == null) {
            throw exception(SESSION_NOT_EXISTS);
        }
        return session;
    }
}
