package com.hc.ticket.module.tkt.service.stock;

/**
 * 库存对账（最小版）
 */
public interface StockReconcileService {

    /**
     * 比对各票档 sold_stock 与有效订单占用，偏差则修正并同步 Redis
     *
     * @return 修正的票档数量
     */
    int reconcileAll();
}
