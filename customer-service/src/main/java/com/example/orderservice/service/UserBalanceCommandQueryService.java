package com.example.orderservice.service;

import com.example.orderservice.entity.UserBalance;

import java.util.concurrent.CompletableFuture;

public interface UserBalanceCommandQueryService {
    CompletableFuture<UserBalance> saveCurrentBalance(String userId);
    CompletableFuture<UserBalance> updateCurrentBalance(String userId, Long amount);
}
