package com.hc.ticket.module.tkt.constants;

/**
 * 票务模块错误码（1_010_xxx_xxx）
 */
public interface ErrorCodeConstants {

    // ========== 演出 1_010_001_xxx ==========
    ErrorCode SHOW_NOT_EXISTS = new ErrorCode(1_010_001_000, "演出不存在");

    // ========== 场次 1_010_002_xxx ==========
    ErrorCode SESSION_NOT_EXISTS = new ErrorCode(1_010_002_000, "场次不存在");
    ErrorCode SESSION_NOT_ON_SALE = new ErrorCode(1_010_002_001, "场次未开售");

    // ========== 票档 1_010_003_xxx ==========
    ErrorCode TIER_NOT_EXISTS = new ErrorCode(1_010_003_000, "票档不存在");
    ErrorCode TIER_OFFLINE = new ErrorCode(1_010_003_001, "票档已下架");
    ErrorCode TIER_SOLD_OUT = new ErrorCode(1_010_003_002, "票档已售罄");
    ErrorCode TIER_STOCK_INVALID = new ErrorCode(1_010_003_003, "总库存不能小于已售/已占用库存");

    // ========== 订单 1_010_004_xxx ==========
    ErrorCode ORDER_NOT_EXISTS = new ErrorCode(1_010_004_000, "订单不存在");
    ErrorCode ORDER_EXPIRED = new ErrorCode(1_010_004_001, "订单已过期");
    ErrorCode ORDER_STATUS_INVALID = new ErrorCode(1_010_004_002, "订单状态不允许该操作");

    // ========== 限购 / 风控 1_010_005_xxx ==========
    ErrorCode USER_BUY_LIMIT_EXCEEDED = new ErrorCode(1_010_005_000, "超过单人限购数量");
    ErrorCode RATE_LIMIT_EXCEEDED = new ErrorCode(1_010_005_001, "请求过于频繁，请稍后再试");

    // ========== 电子票 1_010_006_xxx ==========
    ErrorCode TICKET_NOT_EXISTS = new ErrorCode(1_010_006_000, "电子票不存在");

    // ========== 抢票受理 1_010_007_xxx ==========
    ErrorCode GRAB_RESULT_NOT_EXISTS = new ErrorCode(1_010_007_000, "抢票受理结果不存在或已过期");
    ErrorCode GRAB_QUANTITY_INVALID = new ErrorCode(1_010_007_001, "购买数量不合法");
    ErrorCode USER_ID_REQUIRED = new ErrorCode(1_010_007_002, "用户未登录");
    ErrorCode REDIS_STOCK_NOT_READY = new ErrorCode(1_010_007_003, "库存缓存未就绪，请稍后重试");
    ErrorCode ORDER_CREATE_FAILED = new ErrorCode(1_010_007_004, "建单失败，库存已回补");
    ErrorCode REDIS_BUY_LIMIT_NOT_READY = new ErrorCode(1_010_007_005, "限购缓存未就绪，请稍后重试");

    // ========== 管理端鉴权 1_010_008_xxx ==========
    ErrorCode ADMIN_UNAUTHORIZED = new ErrorCode(1_010_008_000, "管理端未授权");
}
