package com.hc.ticket.module.tkt.redis;

/**
 * Redis Key 约定
 */
public final class TktRedisKeys {

    private TktRedisKeys() {
    }

    /** 票档可售余量 */
    public static String tierRemain(Long tierId) {
        return "tkt:tier:remain:" + tierId;
    }

    /**
     * 用户在某场次票档下的有效占用张数（待支付 + 已支付）
     */
    public static String userBuyLimitUsed(Long userId, Long sessionId, Long tierId) {
        return "tkt:limit:used:" + userId + ":" + sessionId + ":" + tierId;
    }

    /** 抢票受理结果（轮询） */
    public static String grabResult(String acceptToken) {
        return "tkt:grab:result:" + acceptToken;
    }

    /** 演出元数据 */
    public static String metaShow(Long showId) {
        return "tkt:meta:show:" + showId;
    }

    /** 场次元数据 */
    public static String metaSession(Long sessionId) {
        return "tkt:meta:session:" + sessionId;
    }

    /** 票档元数据（不含 soldStock/version） */
    public static String metaTier(Long tierId) {
        return "tkt:meta:tier:" + tierId;
    }

    /** 超时关单 Job 分布式锁 */
    public static String jobCloseOrder() {
        return "tkt:job:close-order";
    }

    /** 对账 Job 分布式锁 */
    public static String jobReconcile() {
        return "tkt:job:reconcile";
    }
}
