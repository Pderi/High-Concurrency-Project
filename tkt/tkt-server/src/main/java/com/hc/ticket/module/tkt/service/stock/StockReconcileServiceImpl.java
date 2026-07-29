package com.hc.ticket.module.tkt.service.stock;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hc.ticket.module.tkt.dal.dataobject.stock.StockLedgerDO;
import com.hc.ticket.module.tkt.dal.dataobject.tier.TierDO;
import com.hc.ticket.module.tkt.dal.mysql.order.OrderMapper;
import com.hc.ticket.module.tkt.dal.mysql.stock.StockLedgerMapper;
import com.hc.ticket.module.tkt.dal.mysql.tier.TierMapper;
import com.hc.ticket.module.tkt.enums.StockChangeTypeEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class StockReconcileServiceImpl implements StockReconcileService {

    @Resource
    private TierMapper tierMapper;
    @Resource
    private OrderMapper orderMapper;
    @Resource
    private StockLedgerMapper stockLedgerMapper;
    @Resource
    private TierStockRedisService tierStockRedisService;
    @Resource
    private PlatformTransactionManager transactionManager;

    @Override
    public int reconcileAll() {
        List<TierDO> tiers = tierMapper.selectList(new LambdaQueryWrapper<>());
        int fixed = 0;
        for (TierDO tier : tiers) {
            try {
                if (reconcileOne(tier.getId())) {
                    fixed++;
                }
            } catch (Exception ex) {
                log.warn("[Reconcile] failed tierId={}", tier.getId(), ex);
            }
        }
        return fixed;
    }

    private boolean reconcileOne(Long tierId) {
        TransactionTemplate tx = new TransactionTemplate(transactionManager);
        Boolean fixed = tx.execute(status -> {
            TierDO tier = tierMapper.selectById(tierId);
            if (tier == null) {
                return false;
            }
            int expected = orderMapper.sumActiveQuantityByTierId(tierId);
            int actual = tier.getSoldStock() == null ? 0 : tier.getSoldStock();
            if (expected == actual) {
                return false;
            }
            log.warn("[Reconcile] tierId={} sold_stock={} expected={} delta={}",
                    tierId, actual, expected, expected - actual);
            tierMapper.updateSoldStock(tierId, expected);
            insertReconcileLedger(tier, actual, expected);
            return true;
        });
        if (!Boolean.TRUE.equals(fixed)) {
            return false;
        }
        try {
            tierStockRedisService.syncRemainFromDb(tierId);
        } catch (Exception ex) {
            log.error("[Reconcile] sync redis remain failed tierId={}", tierId, ex);
        }
        return true;
    }

    private void insertReconcileLedger(TierDO tier, int beforeSold, int afterSold) {
        StockLedgerDO ledger = new StockLedgerDO();
        ledger.setTierId(tier.getId());
        ledger.setSessionId(tier.getSessionId());
        ledger.setChangeType(StockChangeTypeEnum.RECONCILE_FIX.getType());
        ledger.setDelta(afterSold - beforeSold);
        ledger.setBeforeSold(beforeSold);
        ledger.setAfterSold(afterSold);
        ledger.setOrderId(null);
        ledger.setRemark("reconcile sold vs active orders");
        ledger.setCreator("system");
        ledger.setCreateTime(LocalDateTime.now());
        ledger.setTenantId(0L);
        stockLedgerMapper.insert(ledger);
    }
}
