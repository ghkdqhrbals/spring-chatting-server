package com.example.orderservice.service;

import com.example.orderservice.entity.UserBalance;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface UserBalanceCommandQueryService {
    CompletableFuture<UserBalance> saveUserBalance(String userId, UUID eventId);
    CompletableFuture<UserBalance> removeUserBalance(String userId);
    CompletableFuture<UserBalance> updateUserBalance(String userId, Long amount);
}
