package com.hc.ticket.module.tkt.service.stock;

/**
 * 用户限购占用：Redis 为主，Key miss 时回源 MySQL 初始化。
 */
public interface UserBuyLimitRedisService {

    /**
     * 懒加载：Key 不存在时用 DB sumActiveQuantity SETNX。
     */
    void ensureInitialized(Long userId, Long sessionId, Long tierId);

    /**
     * Lua 原子校验并增加占用；超限抛 USER_BUY_LIMIT_EXCEEDED。
     */
    void tryAcquire(Long userId, Long sessionId, Long tierId, int quantity, int perUserLimit);

    /**
     * 回补占用（建单失败 / 幂等回补 / 超时关单）。
     */
    void rollback(Long userId, Long sessionId, Long tierId, int quantity);
}
