package com.example.shopuserservice.domain.user.service;

import com.example.shopuserservice.domain.user.data.User;
import com.example.shopuserservice.domain.user.data.UserTransactions;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface UserReadService {
    CompletableFuture<Optional<User>> getUserById(String id);
    CompletableFuture<List<User>> getAllUser();
    CompletableFuture<Iterable<UserTransactions>> getAllUserAddTransaction();
    CompletableFuture<Iterable<UserTransactions>> getRecentUserAddTransaction(String id);
}
