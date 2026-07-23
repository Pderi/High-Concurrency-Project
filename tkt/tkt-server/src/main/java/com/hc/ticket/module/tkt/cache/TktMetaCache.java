package com.hc.ticket.module.tkt.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hc.ticket.framework.common.util.object.BeanUtils;
import com.hc.ticket.module.tkt.config.TktProperties;
import com.hc.ticket.module.tkt.dal.dataobject.session.SessionDO;
import com.hc.ticket.module.tkt.dal.dataobject.show.ShowDO;
import com.hc.ticket.module.tkt.dal.dataobject.tier.TierDO;
import com.hc.ticket.module.tkt.dal.mysql.session.SessionMapper;
import com.hc.ticket.module.tkt.dal.mysql.show.ShowMapper;
import com.hc.ticket.module.tkt.dal.mysql.tier.TierMapper;
import com.hc.ticket.module.tkt.redis.TktRedisKeys;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

/**
 * 演出 / 场次 / 票档元数据 Redis 缓存。
 * <p>
 * DB 权威；管理端写后 SET 最新快照（预热）；删除则 DEL；TTL 默认 7 天。
 */
@Component
public class TktMetaCache {

    /** 默认 7 天，避免开售中周期性回源 */
    private static final int DEFAULT_META_TTL_SECONDS = 7 * 24 * 3600;

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private TktProperties tktProperties;
    @Resource
    private ShowMapper showMapper;
    @Resource
    private SessionMapper sessionMapper;
    @Resource
    private TierMapper tierMapper;

    public ShowDO getShow(Long id) {
        String key = TktRedisKeys.metaShow(id);
        ShowDO cached = get(key, ShowDO.class);
        if (cached != null) {
            return cached;
        }
        ShowDO show = showMapper.selectById(id);
        if (show != null) {
            put(key, show);
        }
        return show;
    }

    /** 从 DB 加载并 SET（管理端变更后预热） */
    public void refreshShow(Long id) {
        if (id == null) {
            return;
        }
        ShowDO show = showMapper.selectById(id);
        if (show == null) {
            evictShow(id);
            return;
        }
        put(TktRedisKeys.metaShow(id), show);
    }

    public void evictShow(Long id) {
        if (id != null) {
            stringRedisTemplate.delete(TktRedisKeys.metaShow(id));
        }
    }

    public SessionDO getSession(Long id) {
        String key = TktRedisKeys.metaSession(id);
        SessionDO cached = get(key, SessionDO.class);
        if (cached != null) {
            return cached;
        }
        SessionDO session = sessionMapper.selectById(id);
        if (session != null) {
            put(key, session);
        }
        return session;
    }

    public void refreshSession(Long id) {
        if (id == null) {
            return;
        }
        SessionDO session = sessionMapper.selectById(id);
        if (session == null) {
            evictSession(id);
            return;
        }
        put(TktRedisKeys.metaSession(id), session);
    }

    public void evictSession(Long id) {
        if (id != null) {
            stringRedisTemplate.delete(TktRedisKeys.metaSession(id));
        }
    }

    public TierDO getTier(Long id) {
        String key = TktRedisKeys.metaTier(id);
        TierDO cached = get(key, TierDO.class);
        if (cached != null) {
            return cached;
        }
        TierDO tier = tierMapper.selectById(id);
        if (tier != null) {
            put(key, toMetaTier(tier));
        }
        return tier;
    }

    public void refreshTier(Long id) {
        if (id == null) {
            return;
        }
        TierDO tier = tierMapper.selectById(id);
        if (tier == null) {
            evictTier(id);
            return;
        }
        put(TktRedisKeys.metaTier(id), toMetaTier(tier));
    }

    public void evictTier(Long id) {
        if (id != null) {
            stringRedisTemplate.delete(TktRedisKeys.metaTier(id));
        }
    }

    /**
     * 开售前/场次变更后预热：场次 + 所属演出 + 该场次下全部票档元数据
     */
    public void warmSession(Long sessionId) {
        if (sessionId == null) {
            return;
        }
        SessionDO session = sessionMapper.selectById(sessionId);
        if (session == null) {
            evictSession(sessionId);
            return;
        }
        put(TktRedisKeys.metaSession(sessionId), session);
        if (session.getShowId() != null) {
            refreshShow(session.getShowId());
        }
        List<TierDO> tiers = tierMapper.selectListBySessionId(sessionId);
        for (TierDO tier : tiers) {
            put(TktRedisKeys.metaTier(tier.getId()), toMetaTier(tier));
        }
    }

    private TierDO toMetaTier(TierDO tier) {
        TierDO toCache = BeanUtils.toBean(tier, TierDO.class);
        toCache.setSoldStock(null);
        toCache.setVersion(null);
        return toCache;
    }

    private <T> T get(String key, Class<T> type) {
        String json = stringRedisTemplate.opsForValue().get(key);
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readValue(json, type);
        } catch (JsonProcessingException ex) {
            stringRedisTemplate.delete(key);
            return null;
        }
    }

    private void put(String key, Object value) {
        int ttlSeconds = tktProperties.getCache().getMetaTtlSeconds();
        if (ttlSeconds <= 0) {
            ttlSeconds = DEFAULT_META_TTL_SECONDS;
        }
        try {
            stringRedisTemplate.opsForValue().set(
                    key, objectMapper.writeValueAsString(value), Duration.ofSeconds(ttlSeconds));
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("serialize meta cache failed, key=" + key, ex);
        }
    }
}
