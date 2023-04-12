package com.example.shopuserservice.domain.user.service;

import com.example.shopuserservice.domain.data.User;
import com.example.shopuserservice.domain.data.UserTransaction;
import com.example.shopuserservice.domain.user.repository.UserRepository;
import com.example.shopuserservice.domain.user.repository.UserRepositoryJDBC;
import com.example.shopuserservice.domain.user.repository.UserTransactionRepository;
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
    private final UserTransactionRepository userTransactionRepository;

    public UserReadServiceImpl(UserRepository userRepository, UserTransactionRepository userTransactionRepository) {
        this.userRepository = userRepository;
        this.userTransactionRepository = userTransactionRepository;
    }
    @Override
    @Async
    public CompletableFuture<Optional<User>> getUserById(String id) {
        return CompletableFuture.completedFuture(userRepository.findById(id));
    }

    @Override
    @Async
    public CompletableFuture<List<User>> getAllUser() {
        return CompletableFuture.completedFuture(userRepository.findAll());
    }

    @Override
    @Async
    public CompletableFuture<List<UserTransaction>> getAllUserAddTransaction() {
        return CompletableFuture.completedFuture(userTransactionRepository.findAll());
    }
}
