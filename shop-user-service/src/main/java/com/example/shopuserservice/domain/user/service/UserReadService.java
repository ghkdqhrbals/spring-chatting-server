package com.example.shopuserservice.domain.user.service;

import com.example.shopuserservice.domain.data.User;
import com.example.shopuserservice.domain.data.UserTransaction;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface UserReadService {
    CompletableFuture<Optional<User>> getUserById(String id);
    CompletableFuture<List<User>> getAllUser();
    CompletableFuture<List<UserTransaction>> getAllUserAddTransaction();
    CompletableFuture<List<UserTransaction>> getRecentUserAddTransaction(String id);
}
