package com.hc.ticket.module.tkt.dal.mysql.order;

import com.hc.ticket.framework.common.pojo.PageResult;
import com.hc.ticket.framework.mybatis.core.mapper.BaseMapperX;
import com.hc.ticket.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hc.ticket.module.tkt.controller.admin.order.vo.OrderPageReqVO;
import com.hc.ticket.module.tkt.dal.dataobject.order.OrderDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.util.StringUtils;

@Mapper
public interface OrderMapper extends BaseMapperX<OrderDO> {

    default PageResult<OrderDO> selectPage(OrderPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<OrderDO>()
                .eqIfPresent(OrderDO::getOrderNo, reqVO.getOrderNo())
                .eqIfPresent(OrderDO::getUserId, reqVO.getUserId())
                .eqIfPresent(OrderDO::getSessionId, reqVO.getSessionId())
                .eqIfPresent(OrderDO::getTierId, reqVO.getTierId())
                .eqIfPresent(OrderDO::getOrderStatus, reqVO.getOrderStatus())
                .orderByDesc(OrderDO::getId));
    }

    default OrderDO selectByOrderNo(String orderNo) {
        return selectOne(new LambdaQueryWrapperX<OrderDO>()
                .eq(OrderDO::getOrderNo, orderNo));
    }

    default OrderDO selectByIdempotency(Long userId, Long sessionId, Long tierId, String idempotencyKey) {
        if (!StringUtils.hasText(idempotencyKey)) {
            return null;
        }
        return selectOne(new LambdaQueryWrapperX<OrderDO>()
                .eq(OrderDO::getUserId, userId)
                .eq(OrderDO::getSessionId, sessionId)
                .eq(OrderDO::getTierId, tierId)
                .eq(OrderDO::getIdempotencyKey, idempotencyKey));
    }

    /**
     * 待支付(10) + 已支付(20) 张数之和
     */
    @Select("SELECT IFNULL(SUM(quantity), 0) FROM tkt_order "
            + "WHERE user_id = #{userId} AND session_id = #{sessionId} AND tier_id = #{tierId} "
            + "AND order_status IN (10, 20) AND deleted = 0")
    int sumActiveQuantity(@Param("userId") Long userId,
                          @Param("sessionId") Long sessionId,
                          @Param("tierId") Long tierId);
}
