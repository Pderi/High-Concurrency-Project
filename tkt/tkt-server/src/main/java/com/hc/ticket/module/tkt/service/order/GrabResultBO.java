package com.hc.ticket.module.tkt.service.order;

import com.hc.ticket.module.tkt.enums.GrabResultStatusEnum;
import lombok.Data;

/**
 * 抢票受理结果（供轮询）
 */
@Data
public class GrabResultBO {

    private Integer status;
    private String orderNo;
    private Integer errorCode;
    private String errorMsg;

    public static GrabResultBO pending() {
        GrabResultBO bo = new GrabResultBO();
        bo.setStatus(GrabResultStatusEnum.PENDING.getStatus());
        return bo;
    }

    public static GrabResultBO success(String orderNo) {
        GrabResultBO bo = new GrabResultBO();
        bo.setStatus(GrabResultStatusEnum.SUCCESS.getStatus());
        bo.setOrderNo(orderNo);
        return bo;
    }

    public static GrabResultBO fail(Integer errorCode, String errorMsg) {
        GrabResultBO bo = new GrabResultBO();
        bo.setStatus(GrabResultStatusEnum.FAIL.getStatus());
        bo.setErrorCode(errorCode);
        bo.setErrorMsg(errorMsg);
        return bo;
    }
}
