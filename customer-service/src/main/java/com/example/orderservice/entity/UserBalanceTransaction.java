package com.example.orderservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserBalanceTransaction {

    @Id
    private Long orderId;

    private String userId;

    private Long amount;

    private LocalDateTime createdAt;
}
