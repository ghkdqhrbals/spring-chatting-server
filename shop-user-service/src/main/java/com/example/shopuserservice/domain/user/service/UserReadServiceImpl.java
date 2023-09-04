package com.example.shopuserservice.domain.user.service;

import com.example.shopuserservice.domain.data.User;
import com.example.shopuserservice.domain.data.UserTransactions;
import com.example.shopuserservice.domain.user.repository.UserRepository;
import com.example.shopuserservice.domain.user.redisrepository.UserTransactionRedisRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;


@Slf4j
@Service
public class UserReadServiceImpl implements UserReadService{
    private final UserRepository userRepository;
    private final UserTransactionRedisRepository userTransactionRepository;

    public UserReadServiceImpl(UserRepository userRepository, UserTransactionRedisRepository userTransactionRepository) {
        this.userRepository = userRepository;
        this.userTransactionRepository = userTransactionRepository;
    }
    @Override
    public CompletableFuture<Optional<User>> getUserById(String id) {
        return CompletableFuture.completedFuture(userRepository.findById(id));
    }

    @Override
    public CompletableFuture<List<User>> getAllUser() {
        return CompletableFuture.completedFuture(userRepository.findAll());
    }

    @Override
    public CompletableFuture<Iterable<UserTransactions>> getAllUserAddTransaction() {
        return CompletableFuture.completedFuture(userTransactionRepository.findAll());
    }

    @Override
    public CompletableFuture<Iterable<UserTransactions>> getRecentUserAddTransaction(String id) {
        return CompletableFuture.completedFuture(userTransactionRepository.findAll());
    }
}
