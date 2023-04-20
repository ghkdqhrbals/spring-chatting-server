package com.example.orderservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class UserBalance {
    @Id
    @Column(name = "USER_ID", unique = true)
    private String userId;
    private Long userBalance;

    public UserBalance(String userId, Long userBalance) {
        this.userId = userId;
        this.userBalance = userBalance;
    }
}


