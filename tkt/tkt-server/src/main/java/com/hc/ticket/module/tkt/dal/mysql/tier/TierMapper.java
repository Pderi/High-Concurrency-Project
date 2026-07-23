package com.hc.ticket.module.tkt.dal.mysql.tier;

import com.hc.ticket.framework.common.pojo.PageResult;
import com.hc.ticket.framework.mybatis.core.mapper.BaseMapperX;
import com.hc.ticket.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hc.ticket.module.tkt.controller.admin.tier.vo.TierPageReqVO;
import com.hc.ticket.module.tkt.dal.dataobject.tier.TierDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

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

    /**
     * 条件增加 sold_stock，防止超卖
     *
     * @return 影响行数，0 表示库存不足或并发失败
     */
    @Update("UPDATE tkt_tier SET sold_stock = sold_stock + #{qty}, version = version + 1, "
            + "update_time = NOW() "
            + "WHERE id = #{id} AND deleted = 0 AND sold_stock + #{qty} <= total_stock")
    int increaseSoldStock(@Param("id") Long id, @Param("qty") int qty);
}
