package com.example.orderservice.service;

import com.example.commondto.events.user.UserResponseStatus;
import com.example.commondto.events.user.UserStatus;
import com.example.orderservice.entity.UserBalance;
import com.example.orderservice.entity.UserBalanceTransaction;
import com.example.orderservice.repository.UserBalanceRepository;
import com.example.orderservice.repository.UserBalanceTransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class UserBalanceCommandQueryServiceImpl implements UserBalanceCommandQueryService{

    private final UserBalanceRepository userBalanceRepository;
    private final UserBalanceTransactionRepository userBalanceTransactionRepository;

    public UserBalanceCommandQueryServiceImpl(UserBalanceRepository userBalanceRepository, UserBalanceTransactionRepository userBalanceTransactionRepository) {
        this.userBalanceRepository = userBalanceRepository;
        this.userBalanceTransactionRepository = userBalanceTransactionRepository;
    }

    @Override
    @Async
    @Transactional
    public CompletableFuture<UserBalance> saveUserBalance(String userId, UUID eventId) {

        try {
            userBalanceTransactionRepository.save(new UserBalanceTransaction(
                    eventId,
                    userId,
                    UserResponseStatus.USER_APPEND.name()
            ));
        }catch (Exception e){
            return CompletableFuture.failedFuture(new RuntimeException("이벤트 ID unique-violation"));
        }

        Optional<UserBalance> findUserBalance = userBalanceRepository.findById(userId);
        UserBalance savedUserBalance = null;
        if (findUserBalance.isPresent()){
            return CompletableFuture.failedFuture(new RuntimeException("사용자 Balance가 이미 존재합니다"));
        }else{
            log.info("저장!");
            savedUserBalance = userBalanceRepository.save(new UserBalance(userId, 0L));
        }
        return CompletableFuture.completedFuture(savedUserBalance);
    }

    @Override
    @Async
    @Transactional
    public CompletableFuture<UserBalance> removeUserBalance(String userId) {
        Optional<UserBalance> findUserBalance = userBalanceRepository.findById(userId);

        if (findUserBalance.isPresent()){
            userBalanceRepository.delete(findUserBalance.get());
            return CompletableFuture.completedFuture(findUserBalance.get());
        }
        return CompletableFuture.failedFuture(new RuntimeException());
    }

    @Override
    public CompletableFuture<UserBalance> updateUserBalance(String userId, Long amount) {
        return null;
    }
}
