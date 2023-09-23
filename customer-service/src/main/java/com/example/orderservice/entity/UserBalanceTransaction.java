package com.example.orderservice.entity;

import com.example.commondto.events.user.UserStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserBalanceTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @Column(name = "EVENT_ID", unique = true)
    private UUID eventId;
    private String userId;
    private UserStatus customerStatus;
    private LocalDateTime createdAt = LocalDateTime.now();

    public UserBalanceTransaction(UUID eventId, String userId, UserStatus customerStatus) {
        this.eventId = eventId;
        this.userId = userId;
        this.customerStatus = customerStatus;
    }
}
