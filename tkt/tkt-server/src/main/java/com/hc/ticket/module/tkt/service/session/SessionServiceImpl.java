package com.hc.ticket.module.tkt.service.session;

import com.hc.ticket.framework.common.pojo.PageResult;
import com.hc.ticket.framework.common.util.object.BeanUtils;
import com.hc.ticket.module.tkt.cache.TktMetaCache;
import com.hc.ticket.module.tkt.controller.admin.session.vo.SessionAddReqVO;
import com.hc.ticket.module.tkt.controller.admin.session.vo.SessionPageReqVO;
import com.hc.ticket.module.tkt.controller.admin.session.vo.SessionPageRespVO;
import com.hc.ticket.module.tkt.controller.admin.session.vo.SessionRespVO;
import com.hc.ticket.module.tkt.controller.admin.session.vo.SessionUpdateReqVO;
import com.hc.ticket.module.tkt.controller.app.session.vo.AppSessionDetailRespVO;
import com.hc.ticket.module.tkt.controller.app.session.vo.AppSessionTierRespVO;
import com.hc.ticket.module.tkt.dal.dataobject.session.SessionDO;
import com.hc.ticket.module.tkt.dal.dataobject.show.ShowDO;
import com.hc.ticket.module.tkt.dal.dataobject.tier.TierDO;
import com.hc.ticket.module.tkt.dal.mysql.session.SessionMapper;
import com.hc.ticket.module.tkt.dal.mysql.tier.TierMapper;
import com.hc.ticket.module.tkt.enums.ShowStatusEnum;
import com.hc.ticket.module.tkt.enums.TierStatusEnum;
import com.hc.ticket.module.tkt.service.show.ShowService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;

import static com.hc.ticket.framework.common.exception.ServiceExceptionUtil.exception;
import static com.hc.ticket.module.tkt.constants.ErrorCodeConstants.SESSION_NOT_EXISTS;

@Service
@Validated
public class SessionServiceImpl implements SessionService {

    @Resource
    private SessionMapper sessionMapper;
    @Resource
    private TierMapper tierMapper;
    @Resource
    private ShowService showService;
    @Resource
    private TktMetaCache tktMetaCache;

    @Override
    public Long createSession(SessionAddReqVO reqVO) {
        showService.validateShowExists(reqVO.getShowId());
        SessionDO session = BeanUtils.toBean(reqVO, SessionDO.class);
        if (session.getTenantId() == null) {
            session.setTenantId(0L);
        }
        sessionMapper.insert(session);
        tktMetaCache.refreshSession(session.getId());
        return session.getId();
    }

    @Override
    public void updateSession(SessionUpdateReqVO reqVO) {
        validateSessionExists(reqVO.getId());
        showService.validateShowExists(reqVO.getShowId());
        SessionDO updateObj = BeanUtils.toBean(reqVO, SessionDO.class);
        sessionMapper.updateById(updateObj);
        // 开售/改场次：预热场次 + 演出 + 票档元数据
        tktMetaCache.warmSession(reqVO.getId());
    }

    @Override
    public void deleteSession(Long id) {
        validateSessionExists(id);
        sessionMapper.deleteById(id);
        tktMetaCache.evictSession(id);
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
    public AppSessionDetailRespVO getAppSessionDetail(Long id) {
        SessionDO session = validateSessionExists(id);
        ShowDO show = showService.validateShowExists(session.getShowId());
        if (!ShowStatusEnum.PUBLISHED.getStatus().equals(show.getStatus())) {
            throw exception(SESSION_NOT_EXISTS);
        }

        AppSessionDetailRespVO detail = BeanUtils.toBean(session, AppSessionDetailRespVO.class);
        // 场次 / 所属演出：Redis 元数据；票档列表含 soldStock，余票须直查 DB
        List<TierDO> tiers = tierMapper.selectListBySessionId(session.getId());
        List<AppSessionTierRespVO> tierVos = new ArrayList<>();
        for (TierDO tier : tiers) {
            if (!TierStatusEnum.ONLINE.getStatus().equals(tier.getStatus())) {
                continue;
            }
            AppSessionTierRespVO tierVo = BeanUtils.toBean(tier, AppSessionTierRespVO.class);
            int remain = tier.getTotalStock() - tier.getSoldStock();
            tierVo.setRemainStock(Math.max(remain, 0));
            tierVos.add(tierVo);
        }
        detail.setTiers(tierVos);
        return detail;
    }

    @Override
    public void clearAppSessionDetailCache(Long sessionId) {
        // 票档变更后刷新场次元数据（不删，保持开售可读）
        tktMetaCache.refreshSession(sessionId);
    }

    @Override
    public SessionDO validateSessionExists(Long id) {
        SessionDO session = tktMetaCache.getSession(id);
        if (session == null) {
            throw exception(SESSION_NOT_EXISTS);
        }
        return session;
    }
}
