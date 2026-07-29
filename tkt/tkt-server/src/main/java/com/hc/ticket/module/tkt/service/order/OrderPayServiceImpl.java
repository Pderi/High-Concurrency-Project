package com.hc.ticket.module.tkt.service.order;

import com.hc.ticket.framework.common.pojo.PageResult;
import com.hc.ticket.framework.common.util.object.BeanUtils;
import com.hc.ticket.module.tkt.controller.app.order.vo.AppOrderPageReqVO;
import com.hc.ticket.module.tkt.controller.app.order.vo.AppOrderPageRespVO;
import com.hc.ticket.module.tkt.controller.app.order.vo.AppOrderRespVO;
import com.hc.ticket.module.tkt.dal.dataobject.order.OrderDO;
import com.hc.ticket.module.tkt.dal.dataobject.stock.StockLedgerDO;
import com.hc.ticket.module.tkt.dal.dataobject.tier.TierDO;
import com.hc.ticket.module.tkt.dal.mysql.order.OrderMapper;
import com.hc.ticket.module.tkt.dal.mysql.stock.StockLedgerMapper;
import com.hc.ticket.module.tkt.dal.mysql.tier.TierMapper;
import com.hc.ticket.module.tkt.enums.OrderStatusEnum;
import com.hc.ticket.module.tkt.enums.PayChannelEnum;
import com.hc.ticket.module.tkt.enums.StockChangeTypeEnum;
import com.hc.ticket.module.tkt.service.ticket.TicketService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;

import static com.hc.ticket.framework.common.exception.ServiceExceptionUtil.exception;
import static com.hc.ticket.module.tkt.constants.ErrorCodeConstants.ORDER_EXPIRED;
import static com.hc.ticket.module.tkt.constants.ErrorCodeConstants.ORDER_NOT_EXISTS;
import static com.hc.ticket.module.tkt.constants.ErrorCodeConstants.ORDER_STATUS_INVALID;
import static com.hc.ticket.module.tkt.constants.ErrorCodeConstants.USER_ID_REQUIRED;

@Service
@Validated
public class OrderPayServiceImpl implements OrderPayService {

    @Resource
    private OrderMapper orderMapper;
    @Resource
    private TierMapper tierMapper;
    @Resource
    private StockLedgerMapper stockLedgerMapper;
    @Resource
    private TicketService ticketService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AppOrderRespVO pay(Long userId, String orderNo) {
        if (userId == null) {
            throw exception(USER_ID_REQUIRED);
        }
        OrderDO order = orderMapper.selectByOrderNo(orderNo);
        if (order == null || !userId.equals(order.getUserId())) {
            throw exception(ORDER_NOT_EXISTS);
        }

        // 幂等：已支付直接返回
        if (OrderStatusEnum.PAID.getStatus().equals(order.getOrderStatus())) {
            ticketService.createTicketsForOrder(order);
            return BeanUtils.toBean(order, AppOrderRespVO.class);
        }
        if (!OrderStatusEnum.WAIT_PAY.getStatus().equals(order.getOrderStatus())) {
            throw exception(ORDER_STATUS_INVALID);
        }
        LocalDateTime now = LocalDateTime.now();
        if (order.getPayDeadline() != null && !now.isBefore(order.getPayDeadline())) {
            throw exception(ORDER_EXPIRED);
        }

        int rows = orderMapper.updateToPaid(order.getId(), PayChannelEnum.MOCK.getChannel(), now);
        if (rows == 0) {
            OrderDO latest = orderMapper.selectById(order.getId());
            if (latest != null && OrderStatusEnum.PAID.getStatus().equals(latest.getOrderStatus())
                    && userId.equals(latest.getUserId())) {
                ticketService.createTicketsForOrder(latest);
                return BeanUtils.toBean(latest, AppOrderRespVO.class);
            }
            if (latest != null && OrderStatusEnum.CLOSED.getStatus().equals(latest.getOrderStatus())) {
                throw exception(ORDER_STATUS_INVALID);
            }
            throw exception(ORDER_EXPIRED);
        }

        OrderDO paid = orderMapper.selectById(order.getId());
        ticketService.createTicketsForOrder(paid);
        insertPayConfirmLedger(paid);
        return BeanUtils.toBean(paid, AppOrderRespVO.class);
    }

    @Override
    public PageResult<AppOrderPageRespVO> getMyOrderPage(Long userId, AppOrderPageReqVO reqVO) {
        if (userId == null) {
            throw exception(USER_ID_REQUIRED);
        }
        PageResult<OrderDO> page = orderMapper.selectAppPage(userId, reqVO.getOrderStatus(), reqVO);
        return BeanUtils.toBean(page, AppOrderPageRespVO.class);
    }

    private void insertPayConfirmLedger(OrderDO order) {
        TierDO tier = tierMapper.selectById(order.getTierId());
        int sold = tier == null || tier.getSoldStock() == null ? 0 : tier.getSoldStock();
        StockLedgerDO ledger = new StockLedgerDO();
        ledger.setTierId(order.getTierId());
        ledger.setSessionId(order.getSessionId());
        ledger.setChangeType(StockChangeTypeEnum.PAY_CONFIRM.getType());
        // 首期 sold_stock 在下单预占时已计入，支付确认不改变 sold
        ledger.setDelta(0);
        ledger.setBeforeSold(sold);
        ledger.setAfterSold(sold);
        ledger.setOrderId(order.getId());
        ledger.setRemark("mock pay confirm");
        ledger.setCreator(String.valueOf(order.getUserId()));
        ledger.setCreateTime(LocalDateTime.now());
        ledger.setTenantId(0L);
        stockLedgerMapper.insert(ledger);
    }
}
