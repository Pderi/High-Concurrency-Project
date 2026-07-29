package com.hc.ticket.module.tkt.dal.mysql.order;

import com.hc.ticket.framework.common.pojo.PageParam;
import com.hc.ticket.framework.common.pojo.PageResult;
import com.hc.ticket.framework.mybatis.core.mapper.BaseMapperX;
import com.hc.ticket.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hc.ticket.module.tkt.controller.admin.order.vo.OrderPageReqVO;
import com.hc.ticket.module.tkt.dal.dataobject.order.OrderDO;
import com.hc.ticket.module.tkt.enums.OrderStatusEnum;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

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

    default PageResult<OrderDO> selectAppPage(Long userId, Integer orderStatus, PageParam pageParam) {
        return selectPage(pageParam, new LambdaQueryWrapperX<OrderDO>()
                .eqIfPresent(OrderDO::getUserId, userId)
                .eqIfPresent(OrderDO::getOrderStatus, orderStatus)
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
     * 待支付且已过支付截止时间的订单（关单扫描）
     */
    default List<OrderDO> selectExpiredWaitPay(LocalDateTime now, int limit) {
        return selectList(new LambdaQueryWrapperX<OrderDO>()
                .eqIfPresent(OrderDO::getOrderStatus, OrderStatusEnum.WAIT_PAY.getStatus())
                .lt(OrderDO::getPayDeadline, now)
                .orderByAsc(OrderDO::getPayDeadline)
                .last("LIMIT " + limit));
    }

    /**
     * 条件支付：仅待支付且未过期可成功
     */
    @Update("UPDATE tkt_order SET order_status = 20, pay_channel = #{payChannel}, pay_time = #{payTime}, "
            + "update_time = NOW() "
            + "WHERE id = #{id} AND deleted = 0 AND order_status = 10 AND pay_deadline > #{payTime}")
    int updateToPaid(@Param("id") Long id,
                     @Param("payChannel") Integer payChannel,
                     @Param("payTime") LocalDateTime payTime);

    /**
     * 条件关单：仅待支付可成功（防与支付并发双写）
     */
    @Update("UPDATE tkt_order SET order_status = 30, close_reason = #{closeReason}, update_time = NOW() "
            + "WHERE id = #{id} AND deleted = 0 AND order_status = 10")
    int updateToClosed(@Param("id") Long id, @Param("closeReason") String closeReason);

    /**
     * 待支付(10) + 已支付(20) 张数之和
     */
    @Select("SELECT IFNULL(SUM(quantity), 0) FROM tkt_order "
            + "WHERE user_id = #{userId} AND session_id = #{sessionId} AND tier_id = #{tierId} "
            + "AND order_status IN (10, 20) AND deleted = 0")
    int sumActiveQuantity(@Param("userId") Long userId,
                          @Param("sessionId") Long sessionId,
                          @Param("tierId") Long tierId);

    /**
     * 票档维度：有效占用张数（待支付 + 已支付）
     */
    @Select("SELECT IFNULL(SUM(quantity), 0) FROM tkt_order "
            + "WHERE tier_id = #{tierId} AND order_status IN (10, 20) AND deleted = 0")
    int sumActiveQuantityByTierId(@Param("tierId") Long tierId);
}
