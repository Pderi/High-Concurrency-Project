package com.hc.ticket.module.tkt.config;

import com.hc.ticket.module.tkt.config.TktProperties.RateLimit;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class RateLimitConfig {

    public static final String GRAB_RATE_LIMITER = "grab";

    @Bean
    public RateLimiterRegistry rateLimiterRegistry(TktProperties tktProperties) {
        RateLimit props = tktProperties.getRateLimit();
        int limit = props.getLimitForPeriod() == null || props.getLimitForPeriod() <= 0
                ? 100 : props.getLimitForPeriod();
        int refreshSeconds = props.getLimitRefreshPeriodSeconds() == null || props.getLimitRefreshPeriodSeconds() <= 0
                ? 1 : props.getLimitRefreshPeriodSeconds();
        RateLimiterConfig config = RateLimiterConfig.custom()
                .limitForPeriod(limit)
                .limitRefreshPeriod(Duration.ofSeconds(refreshSeconds))
                .timeoutDuration(Duration.ZERO)
                .build();
        return RateLimiterRegistry.of(config);
    }

    @Bean
    public RateLimiter grabRateLimiter(RateLimiterRegistry rateLimiterRegistry) {
        return rateLimiterRegistry.rateLimiter(GRAB_RATE_LIMITER);
    }
}
