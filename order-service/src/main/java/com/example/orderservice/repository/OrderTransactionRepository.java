package com.example.orderservice.repository;

import com.example.orderservice.entity.OrderTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderTransactionRepository extends JpaRepository<OrderTransaction, UUID> {

}
