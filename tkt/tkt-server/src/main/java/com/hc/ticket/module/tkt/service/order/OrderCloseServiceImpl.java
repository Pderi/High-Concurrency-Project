package com.hc.ticket.module.tkt.service.order;

import com.hc.ticket.module.tkt.dal.dataobject.order.OrderDO;
import com.hc.ticket.module.tkt.dal.dataobject.stock.StockLedgerDO;
import com.hc.ticket.module.tkt.dal.dataobject.tier.TierDO;
import com.hc.ticket.module.tkt.dal.mysql.order.OrderMapper;
import com.hc.ticket.module.tkt.dal.mysql.stock.StockLedgerMapper;
import com.hc.ticket.module.tkt.dal.mysql.tier.TierMapper;
import com.hc.ticket.module.tkt.enums.OrderStatusEnum;
import com.hc.ticket.module.tkt.enums.StockChangeTypeEnum;
import com.hc.ticket.module.tkt.metrics.TktMetrics;
import com.hc.ticket.module.tkt.service.stock.TierStockRedisService;
import com.hc.ticket.module.tkt.service.stock.UserBuyLimitRedisService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class OrderCloseServiceImpl implements OrderCloseService {

    public static final String CLOSE_REASON_TIMEOUT = "timeout";

    @Resource
    private OrderMapper orderMapper;
    @Resource
    private TierMapper tierMapper;
    @Resource
    private StockLedgerMapper stockLedgerMapper;
    @Resource
    private TierStockRedisService tierStockRedisService;
    @Resource
    private UserBuyLimitRedisService userBuyLimitRedisService;
    @Resource
    private PlatformTransactionManager transactionManager;
    @Resource
    private TktMetrics tktMetrics;

    @Override
    public int closeExpiredOrders(int batchSize) {
        int limit = batchSize <= 0 ? 100 : batchSize;
        List<OrderDO> orders = orderMapper.selectExpiredWaitPay(LocalDateTime.now(), limit);
        int closed = 0;
        for (OrderDO order : orders) {
            try {
                if (closeOne(order.getId())) {
                    closed++;
                }
            } catch (Exception ex) {
                log.warn("[OrderClose] close failed orderId={}", order.getId(), ex);
            }
        }
        tktMetrics.recordOrderClosed(closed);
        return closed;
    }

    @Override
    public boolean closeOne(Long orderId) {
        TransactionTemplate tx = new TransactionTemplate(transactionManager);
        Boolean dbOk = tx.execute(status -> {
            OrderDO order = orderMapper.selectById(orderId);
            if (order == null || !OrderStatusEnum.WAIT_PAY.getStatus().equals(order.getOrderStatus())) {
                return false;
            }
            int updated = orderMapper.updateToClosed(order.getId(), CLOSE_REASON_TIMEOUT);
            if (updated == 0) {
                return false;
            }
            int qty = order.getQuantity() == null ? 0 : order.getQuantity();
            TierDO tier = tierMapper.selectById(order.getTierId());
            int beforeSold = tier == null || tier.getSoldStock() == null ? 0 : tier.getSoldStock();
            int stockRows = tierMapper.decreaseSoldStock(order.getTierId(), qty);
            if (stockRows == 0) {
                log.error("[OrderClose] decreaseSoldStock failed orderId={} tierId={} qty={}",
                        order.getId(), order.getTierId(), qty);
                status.setRollbackOnly();
                return false;
            }
            insertTimeoutLedger(order, beforeSold, beforeSold - qty);
            return true;
        });
        if (!Boolean.TRUE.equals(dbOk)) {
            return false;
        }
        OrderDO closed = orderMapper.selectById(orderId);
        if (closed != null && closed.getQuantity() != null && closed.getQuantity() > 0) {
            try {
                tierStockRedisService.rollback(closed.getTierId(), closed.getQuantity());
            } catch (Exception ex) {
                log.error("[OrderClose] redis stock rollback failed orderId={} tierId={}",
                        orderId, closed.getTierId(), ex);
            }
            try {
                userBuyLimitRedisService.rollback(
                        closed.getUserId(), closed.getSessionId(), closed.getTierId(), closed.getQuantity());
            } catch (Exception ex) {
                log.error("[OrderClose] redis buy-limit rollback failed orderId={} userId={} tierId={}",
                        orderId, closed.getUserId(), closed.getTierId(), ex);
            }
        }
        return true;
    }

    private void insertTimeoutLedger(OrderDO order, int beforeSold, int afterSold) {
        StockLedgerDO ledger = new StockLedgerDO();
        ledger.setTierId(order.getTierId());
        ledger.setSessionId(order.getSessionId());
        ledger.setChangeType(StockChangeTypeEnum.TIMEOUT_RELEASE.getType());
        ledger.setDelta(-order.getQuantity());
        ledger.setBeforeSold(beforeSold);
        ledger.setAfterSold(afterSold);
        ledger.setOrderId(order.getId());
        ledger.setRemark("timeout close");
        ledger.setCreator("system");
        ledger.setCreateTime(LocalDateTime.now());
        ledger.setTenantId(0L);
        stockLedgerMapper.insert(ledger);
    }
}
