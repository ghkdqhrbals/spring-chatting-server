package com.example.orderservice.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.orderservice.entity.OrderEntity;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity,Long> {
    OrderEntity findByOrderId(String orderId);
    Iterable<OrderEntity> findAllByUserId(String userId);
}
