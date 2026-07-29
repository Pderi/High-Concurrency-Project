package com.hc.ticket.module.tkt.service.order;

import com.hc.ticket.framework.common.util.object.BeanUtils;
import com.hc.ticket.module.tkt.config.TktProperties;
import com.hc.ticket.module.tkt.controller.app.order.vo.AppOrderGrabReqVO;
import com.hc.ticket.module.tkt.controller.app.order.vo.AppOrderGrabRespVO;
import com.hc.ticket.module.tkt.controller.app.order.vo.AppOrderGrabResultRespVO;
import com.hc.ticket.module.tkt.controller.app.order.vo.AppOrderRespVO;
import com.hc.ticket.module.tkt.dal.dataobject.order.OrderDO;
import com.hc.ticket.module.tkt.dal.dataobject.session.SessionDO;
import com.hc.ticket.module.tkt.dal.dataobject.tier.TierDO;
import com.hc.ticket.module.tkt.dal.mysql.order.OrderMapper;
import com.hc.ticket.module.tkt.enums.GrabResultStatusEnum;
import com.hc.ticket.module.tkt.enums.SessionStatusEnum;
import com.hc.ticket.module.tkt.enums.ShowStatusEnum;
import com.hc.ticket.module.tkt.enums.TierStatusEnum;
import com.hc.ticket.module.tkt.mq.message.OrderCreateMessage;
import com.hc.ticket.module.tkt.mq.producer.OrderCreatePublisher;
import com.hc.ticket.module.tkt.service.ratelimit.GrabRateLimitService;
import com.hc.ticket.module.tkt.service.session.SessionService;
import com.hc.ticket.module.tkt.service.show.ShowService;
import com.hc.ticket.module.tkt.service.stock.TierStockRedisService;
import com.hc.ticket.module.tkt.service.tier.TierService;
import com.hc.ticket.framework.web.TraceIdFilter;
import jakarta.annotation.Resource;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.hc.ticket.framework.common.exception.ServiceExceptionUtil.exception;
import static com.hc.ticket.module.tkt.constants.ErrorCodeConstants.GRAB_QUANTITY_INVALID;
import static com.hc.ticket.module.tkt.constants.ErrorCodeConstants.ORDER_NOT_EXISTS;
import static com.hc.ticket.module.tkt.constants.ErrorCodeConstants.SESSION_NOT_ON_SALE;
import static com.hc.ticket.module.tkt.constants.ErrorCodeConstants.SHOW_NOT_EXISTS;
import static com.hc.ticket.module.tkt.constants.ErrorCodeConstants.TIER_OFFLINE;
import static com.hc.ticket.module.tkt.constants.ErrorCodeConstants.USER_BUY_LIMIT_EXCEEDED;
import static com.hc.ticket.module.tkt.constants.ErrorCodeConstants.USER_ID_REQUIRED;

@Service
@Validated
public class OrderGrabServiceImpl implements OrderGrabService {

    @Resource
    private OrderMapper orderMapper;
    @Resource
    private SessionService sessionService;
    @Resource
    private ShowService showService;
    @Resource
    private TierService tierService;
    @Resource
    private TierStockRedisService tierStockRedisService;
    @Resource
    private OrderCreatePublisher orderCreatePublisher;
    @Resource
    private GrabResultService grabResultService;
    @Resource
    private TktProperties tktProperties;
    @Resource
    private GrabRateLimitService grabRateLimitService;

    @Override
    public AppOrderGrabRespVO grab(Long userId, AppOrderGrabReqVO reqVO) {
        grabRateLimitService.checkPermit();
        if (userId == null) {
            throw exception(USER_ID_REQUIRED);
        }
        if (reqVO.getQuantity() == null || reqVO.getQuantity() < 1) {
            throw exception(GRAB_QUANTITY_INVALID);
        }

        // 幂等：已建单则直接返回成功受理
        OrderDO exists = orderMapper.selectByIdempotency(
                userId, reqVO.getSessionId(), reqVO.getTierId(), reqVO.getIdempotencyKey());
        if (exists != null) {
            String token = exists.getOrderNo();
            grabResultService.saveSuccess(token, exists.getOrderNo());
            return buildResp(token, exists.getOrderNo());
        }

        SessionDO session = sessionService.validateSessionExists(reqVO.getSessionId());
        validateSessionOnSale(session);
        if (!ShowStatusEnum.PUBLISHED.getStatus().equals(
                showService.validateShowExists(session.getShowId()).getStatus())) {
            throw exception(SHOW_NOT_EXISTS);
        }

        TierDO tier = tierService.validateTierExists(reqVO.getTierId());
        if (!tier.getSessionId().equals(session.getId())) {
            throw exception(SESSION_NOT_ON_SALE);
        }
        if (!TierStatusEnum.ONLINE.getStatus().equals(tier.getStatus())) {
            throw exception(TIER_OFFLINE);
        }
        if (reqVO.getQuantity() > tier.getPerUserLimit()) {
            throw exception(USER_BUY_LIMIT_EXCEEDED);
        }
        int used = orderMapper.sumActiveQuantity(userId, session.getId(), tier.getId());
        if (used + reqVO.getQuantity() > tier.getPerUserLimit()) {
            throw exception(USER_BUY_LIMIT_EXCEEDED);
        }

        String acceptToken = UUID.randomUUID().toString().replace("-", "");
        OrderCreateMessage message = new OrderCreateMessage();
        message.setMessageId(acceptToken);
        message.setUserId(userId);
        message.setSessionId(session.getId());
        message.setTierId(tier.getId());
        message.setQuantity(reqVO.getQuantity());
        message.setUnitPriceCent(tier.getPriceCent());
        message.setIdempotencyKey(reqVO.getIdempotencyKey());
        message.setRedisToken(acceptToken);

        tierStockRedisService.deduct(tier.getId(), reqVO.getQuantity());
        grabResultService.savePending(acceptToken);
        orderCreatePublisher.publish(message);

        GrabResultBO result = grabResultService.get(acceptToken);
        String orderNo = null;
        if (GrabResultStatusEnum.SUCCESS.getStatus().equals(result.getStatus())) {
            orderNo = result.getOrderNo();
        }
        if (StringUtils.hasText(orderNo)) {
            MDC.put(TraceIdFilter.MDC_ORDER_NO, orderNo);
        }
        return buildResp(acceptToken, orderNo);
    }

    @Override
    public AppOrderGrabResultRespVO getGrabResult(String acceptToken) {
        GrabResultBO result = grabResultService.get(acceptToken);
        return BeanUtils.toBean(result, AppOrderGrabResultRespVO.class);
    }

    @Override
    public AppOrderRespVO getMyOrder(Long userId, String orderNo) {
        if (userId == null) {
            throw exception(USER_ID_REQUIRED);
        }
        OrderDO order = orderMapper.selectByOrderNo(orderNo);
        if (order == null || !userId.equals(order.getUserId())) {
            throw exception(ORDER_NOT_EXISTS);
        }
        return BeanUtils.toBean(order, AppOrderRespVO.class);
    }

    private void validateSessionOnSale(SessionDO session) {
        if (!SessionStatusEnum.ON_SALE.getStatus().equals(session.getStatus())) {
            throw exception(SESSION_NOT_ON_SALE);
        }
        LocalDateTime now = LocalDateTime.now();
        if (session.getSaleStartTime() != null && now.isBefore(session.getSaleStartTime())) {
            throw exception(SESSION_NOT_ON_SALE);
        }
        if (session.getSaleEndTime() != null && now.isAfter(session.getSaleEndTime())) {
            throw exception(SESSION_NOT_ON_SALE);
        }
    }

    private AppOrderGrabRespVO buildResp(String acceptToken, String orderNo) {
        AppOrderGrabRespVO resp = new AppOrderGrabRespVO();
        resp.setAcceptToken(acceptToken);
        resp.setOrderNo(orderNo);
        Integer pollSeconds = tktProperties.getGrab().getAsyncPollSeconds();
        resp.setPollSeconds(pollSeconds == null ? 1 : pollSeconds);
        return resp;
    }
}
