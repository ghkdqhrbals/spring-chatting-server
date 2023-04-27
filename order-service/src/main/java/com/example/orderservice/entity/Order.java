package com.example.orderservice.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
public class Order {
    @Id
    private UUID orderId;
    private LocalDateTime createdAt;
    private UUID productId;
    private String userId;
    private Integer amount;
    private Integer price;

}
