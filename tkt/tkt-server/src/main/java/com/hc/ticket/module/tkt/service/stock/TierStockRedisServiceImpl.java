package com.hc.ticket.module.tkt.service.stock;

import com.hc.ticket.module.tkt.dal.dataobject.tier.TierDO;
import com.hc.ticket.module.tkt.dal.mysql.tier.TierMapper;
import com.hc.ticket.module.tkt.metrics.TktMetrics;
import com.hc.ticket.module.tkt.redis.TktRedisKeys;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

import static com.hc.ticket.framework.common.exception.ServiceExceptionUtil.exception;
import static com.hc.ticket.module.tkt.constants.ErrorCodeConstants.REDIS_STOCK_NOT_READY;
import static com.hc.ticket.module.tkt.constants.ErrorCodeConstants.TIER_NOT_EXISTS;
import static com.hc.ticket.module.tkt.constants.ErrorCodeConstants.TIER_SOLD_OUT;

@Service
public class TierStockRedisServiceImpl implements TierStockRedisService {

    private static final DefaultRedisScript<Long> DEDUCT_SCRIPT = new DefaultRedisScript<>();

    static {
        DEDUCT_SCRIPT.setResultType(Long.class);
        DEDUCT_SCRIPT.setScriptText(
                "local remain = redis.call('GET', KEYS[1])\n"
                        + "if remain == false then return -1 end\n"
                        + "remain = tonumber(remain)\n"
                        + "local qty = tonumber(ARGV[1])\n"
                        + "if remain < qty then return 0 end\n"
                        + "redis.call('DECRBY', KEYS[1], qty)\n"
                        + "return 1\n");
    }

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private TierMapper tierMapper;
    @Resource
    private TktMetrics tktMetrics;

    @Override
    public void ensureRemainInitialized(Long tierId) {
        String key = TktRedisKeys.tierRemain(tierId);
        Boolean exists = stringRedisTemplate.hasKey(key);
        if (Boolean.TRUE.equals(exists)) {
            return;
        }
        TierDO tier = tierMapper.selectById(tierId);
        if (tier == null) {
            throw exception(TIER_NOT_EXISTS);
        }
        int remain = Math.max(tier.getTotalStock() - tier.getSoldStock(), 0);
        // SETNX：并发初始化时仅一人成功即可
        stringRedisTemplate.opsForValue().setIfAbsent(key, String.valueOf(remain));
    }

    @Override
    public void deduct(Long tierId, int quantity) {
        ensureRemainInitialized(tierId);
        List<String> keys = Collections.singletonList(TktRedisKeys.tierRemain(tierId));
        Long result = stringRedisTemplate.execute(DEDUCT_SCRIPT, keys, String.valueOf(quantity));
        if (result == null || result == -1L) {
            tktMetrics.recordDeductNotReady();
            throw exception(REDIS_STOCK_NOT_READY);
        }
        if (result == 0L) {
            tktMetrics.recordDeductSoldOut();
            throw exception(TIER_SOLD_OUT);
        }
        tktMetrics.recordDeductSuccess();
    }

    @Override
    public void rollback(Long tierId, int quantity) {
        if (tierId == null || quantity <= 0) {
            return;
        }
        stringRedisTemplate.opsForValue().increment(TktRedisKeys.tierRemain(tierId), quantity);
    }

    @Override
    public void syncRemainFromDb(Long tierId) {
        if (tierId == null) {
            return;
        }
        TierDO tier = tierMapper.selectById(tierId);
        if (tier == null) {
            throw exception(TIER_NOT_EXISTS);
        }
        int remain = Math.max(tier.getTotalStock() - tier.getSoldStock(), 0);
        stringRedisTemplate.opsForValue().set(TktRedisKeys.tierRemain(tierId), String.valueOf(remain));
    }
}
