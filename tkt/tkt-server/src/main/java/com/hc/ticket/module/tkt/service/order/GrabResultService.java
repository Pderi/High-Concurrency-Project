package com.hc.ticket.module.tkt.service.order;

/**
 * 抢票受理结果读写
 */
public interface GrabResultService {

    void savePending(String acceptToken);

    void saveSuccess(String acceptToken, String orderNo);

    void saveFail(String acceptToken, Integer errorCode, String errorMsg);

    GrabResultBO get(String acceptToken);
}
