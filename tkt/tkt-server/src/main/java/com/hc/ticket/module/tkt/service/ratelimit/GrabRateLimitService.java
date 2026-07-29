package com.hc.ticket.module.tkt.service.ratelimit;

import com.hc.ticket.module.tkt.config.TktProperties;
import io.github.resilience4j.ratelimiter.RateLimiter;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import static com.hc.ticket.framework.common.exception.ServiceExceptionUtil.exception;
import static com.hc.ticket.module.tkt.constants.ErrorCodeConstants.RATE_LIMIT_EXCEEDED;

/**
 * 抢票接口限流门禁
 */
@Service
public class GrabRateLimitService {

    @Resource
    private RateLimiter grabRateLimiter;
    @Resource
    private TktProperties tktProperties;

    public void checkPermit() {
        if (!Boolean.TRUE.equals(tktProperties.getRateLimit().getEnabled())) {
            return;
        }
        if (!grabRateLimiter.acquirePermission()) {
            throw exception(RATE_LIMIT_EXCEEDED);
        }
    }
}
