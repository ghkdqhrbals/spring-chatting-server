package com.example.orderservice.service;


import com.example.orderservice.data.OrderEntity;
import com.example.orderservice.dto.OrderDto;

public interface OrderService {
    Iterable<OrderEntity> getAllOrdersByUserId(String userId);
    OrderDto getOrderByOrderId(String orderId);
    OrderDto createOrder(OrderDto orderDto);
}
