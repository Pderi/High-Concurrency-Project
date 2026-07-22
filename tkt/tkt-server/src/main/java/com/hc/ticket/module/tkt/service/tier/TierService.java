package com.hc.ticket.module.tkt.service.tier;

import com.hc.ticket.framework.common.pojo.PageResult;
import com.hc.ticket.module.tkt.controller.admin.tier.vo.TierAddReqVO;
import com.hc.ticket.module.tkt.controller.admin.tier.vo.TierPageReqVO;
import com.hc.ticket.module.tkt.controller.admin.tier.vo.TierPageRespVO;
import com.hc.ticket.module.tkt.controller.admin.tier.vo.TierRespVO;
import com.hc.ticket.module.tkt.controller.admin.tier.vo.TierUpdateReqVO;
import com.hc.ticket.module.tkt.dal.dataobject.tier.TierDO;

/**
 * 票档领域服务
 */
public interface TierService {

    Long createTier(TierAddReqVO reqVO);

    void updateTier(TierUpdateReqVO reqVO);

    void deleteTier(Long id);

    TierRespVO getTier(Long id);

    PageResult<TierPageRespVO> getTierPage(TierPageReqVO reqVO);

    TierDO validateTierExists(Long id);
}
