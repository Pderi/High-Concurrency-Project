package com.hc.ticket.module.tkt.service.order;

import com.hc.ticket.module.tkt.config.TktProperties;
import com.hc.ticket.module.tkt.dal.dataobject.order.OrderDO;
import com.hc.ticket.module.tkt.dal.dataobject.stock.StockLedgerDO;
import com.hc.ticket.module.tkt.dal.dataobject.tier.TierDO;
import com.hc.ticket.module.tkt.dal.mysql.order.OrderMapper;
import com.hc.ticket.module.tkt.dal.mysql.stock.StockLedgerMapper;
import com.hc.ticket.module.tkt.dal.mysql.tier.TierMapper;
import com.hc.ticket.module.tkt.enums.OrderStatusEnum;
import com.hc.ticket.module.tkt.enums.PayChannelEnum;
import com.hc.ticket.module.tkt.enums.StockChangeTypeEnum;
import com.hc.ticket.module.tkt.mq.message.OrderCreateMessage;
import jakarta.annotation.Resource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

import static com.hc.ticket.framework.common.exception.ServiceExceptionUtil.exception;
import static com.hc.ticket.module.tkt.constants.ErrorCodeConstants.ORDER_CREATE_FAILED;
import static com.hc.ticket.module.tkt.constants.ErrorCodeConstants.TIER_NOT_EXISTS;
import static com.hc.ticket.module.tkt.constants.ErrorCodeConstants.TIER_SOLD_OUT;

@Service
public class OrderCreateServiceImpl implements OrderCreateService {

    private static final DateTimeFormatter ORDER_NO_TIME = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    @Resource
    private OrderMapper orderMapper;
    @Resource
    private TierMapper tierMapper;
    @Resource
    private StockLedgerMapper stockLedgerMapper;
    @Resource
    private TktProperties tktProperties;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CreateOrderResult createOrder(OrderCreateMessage message) {
        if (StringUtils.hasText(message.getIdempotencyKey())) {
            OrderDO exists = orderMapper.selectByIdempotency(
                    message.getUserId(), message.getSessionId(), message.getTierId(), message.getIdempotencyKey());
            if (exists != null) {
                // 订单已存在：本次不应再占库存（调用方回补 Redis）
                return new CreateOrderResult(exists.getOrderNo(), true);
            }
        }

        TierDO tier = tierMapper.selectById(message.getTierId());
        if (tier == null) {
            throw exception(TIER_NOT_EXISTS);
        }

        int beforeSold = tier.getSoldStock();
        int rows = tierMapper.increaseSoldStock(tier.getId(), message.getQuantity());
        if (rows == 0) {
            throw exception(TIER_SOLD_OUT);
        }

        OrderDO order = buildOrder(message);
        if (!insertOrder(order, message)) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            OrderDO duplicated = orderMapper.selectByIdempotency(
                    message.getUserId(), message.getSessionId(), message.getTierId(), message.getIdempotencyKey());
            if (duplicated == null) {
                throw exception(ORDER_CREATE_FAILED);
            }
            return new CreateOrderResult(duplicated.getOrderNo(), true);
        }

        insertLedger(message, tier.getId(), beforeSold, order.getId());
        return new CreateOrderResult(order.getOrderNo(), false);
    }

    private OrderDO buildOrder(OrderCreateMessage message) {
        OrderDO order = new OrderDO();
        order.setOrderNo(generateOrderNo());
        order.setUserId(message.getUserId());
        order.setSessionId(message.getSessionId());
        order.setTierId(message.getTierId());
        order.setQuantity(message.getQuantity());
        order.setUnitPriceCent(message.getUnitPriceCent());
        order.setTotalAmountCent(message.getUnitPriceCent() * message.getQuantity());
        order.setOrderStatus(OrderStatusEnum.WAIT_PAY.getStatus());
        order.setPayChannel(PayChannelEnum.NONE.getChannel());
        int payMinutes = tktProperties.getOrder().getPayTimeoutMinutes() == null
                ? 15 : tktProperties.getOrder().getPayTimeoutMinutes();
        order.setPayDeadline(LocalDateTime.now().plusMinutes(payMinutes));
        order.setIdempotencyKey(message.getIdempotencyKey());
        order.setTenantId(0L);
        return order;
    }

    /**
     * @return true 插入成功；false 唯一键冲突
     */
    private boolean insertOrder(OrderDO order, OrderCreateMessage message) {
        try {
            orderMapper.insert(order);
            return true;
        } catch (DuplicateKeyException ex) {
            return false;
        }
    }

    private void insertLedger(OrderCreateMessage message, Long tierId, int beforeSold, Long orderId) {
        StockLedgerDO ledger = new StockLedgerDO();
        ledger.setTierId(tierId);
        ledger.setSessionId(message.getSessionId());
        ledger.setChangeType(StockChangeTypeEnum.ORDER_RESERVE.getType());
        ledger.setDelta(message.getQuantity());
        ledger.setBeforeSold(beforeSold);
        ledger.setAfterSold(beforeSold + message.getQuantity());
        ledger.setOrderId(orderId);
        ledger.setRemark("grab reserve");
        ledger.setCreator(String.valueOf(message.getUserId()));
        ledger.setCreateTime(LocalDateTime.now());
        ledger.setTenantId(0L);
        stockLedgerMapper.insert(ledger);
    }

    private static String generateOrderNo() {
        int rand = ThreadLocalRandom.current().nextInt(100000, 999999);
        return "TKT" + LocalDateTime.now().format(ORDER_NO_TIME) + rand;
    }
}
