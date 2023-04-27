package com.example.orderservice.service;

import com.example.orderservice.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class OrderCommandService {
    private final OrderRepository orderRepository;

    @Autowired
    public OrderCommandService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Mono createOrder(UUID eventId){


    }

}
