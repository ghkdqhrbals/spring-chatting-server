package com.example.shopuserservice.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderServiceClientWithFallbackFactory implements FallbackFactory<OrderServiceClientWithFallback> {
    private final OrderServiceClientWithFallback orderServiceClientWithFallback;

    public OrderServiceClientWithFallbackFactory(OrderServiceClientWithFallback orderServiceClientWithFallback) {
        this.orderServiceClientWithFallback = orderServiceClientWithFallback;
    }

    @Override
    public OrderServiceClientWithFallback create(Throwable cause) {
        log.info("[OrderServiceClientWithFallback] error occurred, {}", cause.getMessage());
        return orderServiceClientWithFallback;
    }

}
