package com.hc.ticket.module.tkt.service.stock;

/**
 * 票档 Redis 预扣库存
 */
public interface TierStockRedisService {

    /**
     * 懒加载初始化余量：totalStock - soldStock
     */
    void ensureRemainInitialized(Long tierId);

    /**
     * Lua 原子预扣
     *
     * @return true 成功；失败抛业务错误码
     */
    void deduct(Long tierId, int quantity);

    /**
     * 回补余量
     */
    void rollback(Long tierId, int quantity);

    /**
     * 将 Redis 余量强制同步为 totalStock - soldStock（对账修正用）
     */
    void syncRemainFromDb(Long tierId);
}
