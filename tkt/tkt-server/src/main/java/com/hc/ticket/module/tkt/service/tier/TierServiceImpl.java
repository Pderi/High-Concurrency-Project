package com.hc.ticket.module.tkt.service.tier;

import com.hc.ticket.framework.common.pojo.PageResult;
import com.hc.ticket.framework.common.util.object.BeanUtils;
import com.hc.ticket.module.tkt.controller.admin.tier.vo.TierAddReqVO;
import com.hc.ticket.module.tkt.controller.admin.tier.vo.TierPageReqVO;
import com.hc.ticket.module.tkt.controller.admin.tier.vo.TierPageRespVO;
import com.hc.ticket.module.tkt.controller.admin.tier.vo.TierRespVO;
import com.hc.ticket.module.tkt.controller.admin.tier.vo.TierUpdateReqVO;
import com.hc.ticket.module.tkt.dal.dataobject.tier.TierDO;
import com.hc.ticket.module.tkt.dal.mysql.tier.TierMapper;
import com.hc.ticket.module.tkt.service.session.SessionService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import static com.hc.ticket.framework.common.exception.ServiceExceptionUtil.exception;
import static com.hc.ticket.module.tkt.constants.ErrorCodeConstants.TIER_NOT_EXISTS;
import static com.hc.ticket.module.tkt.constants.ErrorCodeConstants.TIER_STOCK_INVALID;

@Service
@Validated
public class TierServiceImpl implements TierService {

    @Resource
    private TierMapper tierMapper;
    @Resource
    private SessionService sessionService;

    @Override
    public Long createTier(TierAddReqVO reqVO) {
        sessionService.validateSessionExists(reqVO.getSessionId());
        TierDO tier = BeanUtils.toBean(reqVO, TierDO.class);
        tier.setSoldStock(0);
        tier.setVersion(0);
        if (tier.getPerUserLimit() == null) {
            tier.setPerUserLimit(1);
        }
        if (tier.getTenantId() == null) {
            tier.setTenantId(0L);
        }
        tierMapper.insert(tier);
        return tier.getId();
    }

    @Override
    public void updateTier(TierUpdateReqVO reqVO) {
        TierDO exists = validateTierExists(reqVO.getId());
        sessionService.validateSessionExists(reqVO.getSessionId());
        if (reqVO.getTotalStock() < exists.getSoldStock()) {
            throw exception(TIER_STOCK_INVALID);
        }
        TierDO updateObj = BeanUtils.toBean(reqVO, TierDO.class);
        // 不允许通过管理端直接改写 soldStock / version
        updateObj.setSoldStock(null);
        updateObj.setVersion(null);
        tierMapper.updateById(updateObj);
    }

    @Override
    public void deleteTier(Long id) {
        validateTierExists(id);
        tierMapper.deleteById(id);
    }

    @Override
    public TierRespVO getTier(Long id) {
        return BeanUtils.toBean(validateTierExists(id), TierRespVO.class);
    }

    @Override
    public PageResult<TierPageRespVO> getTierPage(TierPageReqVO reqVO) {
        return BeanUtils.toBean(tierMapper.selectPage(reqVO), TierPageRespVO.class);
    }

    @Override
    public TierDO validateTierExists(Long id) {
        TierDO tier = tierMapper.selectById(id);
        if (tier == null) {
            throw exception(TIER_NOT_EXISTS);
        }
        return tier;
    }
}
