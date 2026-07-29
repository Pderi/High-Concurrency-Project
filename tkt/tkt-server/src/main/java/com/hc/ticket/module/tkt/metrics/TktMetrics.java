package com.hc.ticket.module.tkt.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 抢票核心业务指标
 */
@Component
public class TktMetrics {

    public static final String PREFIX = "tkt.";

    @Resource
    private MeterRegistry meterRegistry;

    private Counter deductSuccess;
    private Counter deductSoldOut;
    private Counter deductNotReady;
    private Counter orderCreateSuccess;
    private Counter orderCreateFail;
    private Counter orderClosed;
    private Timer orderCreateTimer;

    @PostConstruct
    public void init() {
        deductSuccess = Counter.builder(PREFIX + "deduct")
                .tag("result", "success")
                .description("Redis stock deduct success")
                .register(meterRegistry);
        deductSoldOut = Counter.builder(PREFIX + "deduct")
                .tag("result", "sold_out")
                .description("Redis stock deduct sold out")
                .register(meterRegistry);
        deductNotReady = Counter.builder(PREFIX + "deduct")
                .tag("result", "not_ready")
                .description("Redis stock not ready")
                .register(meterRegistry);
        orderCreateSuccess = Counter.builder(PREFIX + "order_create")
                .tag("result", "success")
                .register(meterRegistry);
        orderCreateFail = Counter.builder(PREFIX + "order_create")
                .tag("result", "fail")
                .register(meterRegistry);
        orderClosed = Counter.builder(PREFIX + "order_close")
                .tag("result", "success")
                .register(meterRegistry);
        orderCreateTimer = Timer.builder(PREFIX + "order_create_latency")
                .description("Order create latency")
                .register(meterRegistry);
    }

    public void recordDeductSuccess() {
        deductSuccess.increment();
    }

    public void recordDeductSoldOut() {
        deductSoldOut.increment();
    }

    public void recordDeductNotReady() {
        deductNotReady.increment();
    }

    public void recordOrderCreateSuccess(long costNanos) {
        orderCreateSuccess.increment();
        orderCreateTimer.record(costNanos, TimeUnit.NANOSECONDS);
    }

    public void recordOrderCreateFail() {
        orderCreateFail.increment();
    }

    public void recordOrderClosed(int count) {
        if (count > 0) {
            orderClosed.increment(count);
        }
    }
}
