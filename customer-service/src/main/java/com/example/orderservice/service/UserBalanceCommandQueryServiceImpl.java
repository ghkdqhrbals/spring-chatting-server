package com.example.orderservice.service;

import com.example.orderservice.entity.UserBalance;
import com.example.orderservice.repository.UserBalanceRepository;
import com.example.orderservice.repository.UserBalanceTransactionRepository;

import java.util.concurrent.CompletableFuture;

public class UserBalanceCommandQueryServiceImpl implements UserBalanceCommandQueryService{

    private final UserBalanceRepository userBalanceRepository;
    private final UserBalanceTransactionRepository userBalanceTransactionRepository;

    public UserBalanceCommandQueryServiceImpl(UserBalanceRepository userBalanceRepository, UserBalanceTransactionRepository userBalanceTransactionRepository) {
        this.userBalanceRepository = userBalanceRepository;
        this.userBalanceTransactionRepository = userBalanceTransactionRepository;
    }

    @Override
    public CompletableFuture<UserBalance> saveCurrentBalance(String userId) {
        return null;
    }

    @Override
    public CompletableFuture<UserBalance> updateCurrentBalance(String userId, Long amount) {
        return null;
    }
}
