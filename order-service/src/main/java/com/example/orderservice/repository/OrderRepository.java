package com.example.orderservice.repository;

import com.example.orderservice.data.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface OrderRepository extends JpaRepository<OrderEntity,Long> {

    OrderEntity findByOrderId(String orderId);

    Iterable<OrderEntity> findAllByUserId(String userId);
}
