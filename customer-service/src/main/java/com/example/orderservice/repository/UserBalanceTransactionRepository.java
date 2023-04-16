package com.example.orderservice.repository;

import com.example.orderservice.entity.UserBalanceTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserBalanceTransactionRepository extends JpaRepository<UserBalanceTransaction, Long> {

}
