package com.hc.ticket.module.tkt.service.stock;

import com.hc.ticket.module.tkt.dal.mysql.order.OrderMapper;
import com.hc.ticket.module.tkt.redis.TktRedisKeys;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

import static com.hc.ticket.framework.common.exception.ServiceExceptionUtil.exception;
import static com.hc.ticket.module.tkt.constants.ErrorCodeConstants.REDIS_BUY_LIMIT_NOT_READY;
import static com.hc.ticket.module.tkt.constants.ErrorCodeConstants.USER_BUY_LIMIT_EXCEEDED;

@Service
public class UserBuyLimitRedisServiceImpl implements UserBuyLimitRedisService {

    private static final DefaultRedisScript<Long> ACQUIRE_SCRIPT = new DefaultRedisScript<>();

    static {
        ACQUIRE_SCRIPT.setResultType(Long.class);
        ACQUIRE_SCRIPT.setScriptText(
                "local used = redis.call('GET', KEYS[1])\n"
                        + "if used == false then return -1 end\n"
                        + "used = tonumber(used)\n"
                        + "local qty = tonumber(ARGV[1])\n"
                        + "local limit = tonumber(ARGV[2])\n"
                        + "if used + qty > limit then return 0 end\n"
                        + "redis.call('INCRBY', KEYS[1], qty)\n"
                        + "return 1\n");
    }

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private OrderMapper orderMapper;

    @Override
    public void ensureInitialized(Long userId, Long sessionId, Long tierId) {
        String key = TktRedisKeys.userBuyLimitUsed(userId, sessionId, tierId);
        Boolean exists = stringRedisTemplate.hasKey(key);
        if (Boolean.TRUE.equals(exists)) {
            return;
        }
        int used = orderMapper.sumActiveQuantity(userId, sessionId, tierId);
        stringRedisTemplate.opsForValue().setIfAbsent(key, String.valueOf(Math.max(used, 0)));
    }

    @Override
    public void tryAcquire(Long userId, Long sessionId, Long tierId, int quantity, int perUserLimit) {
        if (userId == null || sessionId == null || tierId == null || quantity <= 0) {
            throw exception(USER_BUY_LIMIT_EXCEEDED);
        }
        if (perUserLimit <= 0) {
            throw exception(USER_BUY_LIMIT_EXCEEDED);
        }
        ensureInitialized(userId, sessionId, tierId);
        List<String> keys = Collections.singletonList(
                TktRedisKeys.userBuyLimitUsed(userId, sessionId, tierId));
        Long result = stringRedisTemplate.execute(
                ACQUIRE_SCRIPT, keys, String.valueOf(quantity), String.valueOf(perUserLimit));
        if (result == null || result == -1L) {
            throw exception(REDIS_BUY_LIMIT_NOT_READY);
        }
        if (result == 0L) {
            throw exception(USER_BUY_LIMIT_EXCEEDED);
        }
    }

    @Override
    public void rollback(Long userId, Long sessionId, Long tierId, int quantity) {
        if (userId == null || sessionId == null || tierId == null || quantity <= 0) {
            return;
        }
        String key = TktRedisKeys.userBuyLimitUsed(userId, sessionId, tierId);
        Long after = stringRedisTemplate.opsForValue().increment(key, -quantity);
        if (after != null && after < 0) {
            stringRedisTemplate.opsForValue().set(key, "0");
        }
    }
}
