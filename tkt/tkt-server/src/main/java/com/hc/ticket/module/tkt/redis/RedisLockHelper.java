package com.hc.ticket.module.tkt.redis;

import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.UUID;

/**
 * 基于 Redis SET NX EX 的简易分布式锁
 */
@Component
public class RedisLockHelper {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * @return 锁 token；未抢到返回 null
     */
    public String tryLock(String key, Duration ttl) {
        String token = UUID.randomUUID().toString();
        Boolean ok = stringRedisTemplate.opsForValue().setIfAbsent(key, token, ttl);
        return Boolean.TRUE.equals(ok) ? token : null;
    }

    /**
     * 仅当 token 匹配时释放，避免误删他人锁
     */
    public void unlock(String key, String token) {
        if (key == null || token == null) {
            return;
        }
        String current = stringRedisTemplate.opsForValue().get(key);
        if (token.equals(current)) {
            stringRedisTemplate.delete(key);
        }
    }
}
