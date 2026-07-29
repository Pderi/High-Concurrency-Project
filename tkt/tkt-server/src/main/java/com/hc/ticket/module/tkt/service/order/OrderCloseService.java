package com.hc.ticket.module.tkt.service.order;

/**
 * 超时关单
 */
public interface OrderCloseService {

    /**
     * 扫描并关闭一批过期未支付订单
     *
     * @return 本批成功关闭数量
     */
    int closeExpiredOrders(int batchSize);

    /**
     * 关闭单笔订单（条件更新，失败则跳过）
     *
     * @return true 关闭成功
     */
    boolean closeOne(Long orderId);
}
