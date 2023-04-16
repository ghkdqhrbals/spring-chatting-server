package com.example.orderservice.service;

import com.example.orderservice.entity.UserBalance;

import java.util.concurrent.CompletableFuture;

public interface UserBalanceReadService {
    CompletableFuture<UserBalance> getCurrentBalance(String userId);
}
