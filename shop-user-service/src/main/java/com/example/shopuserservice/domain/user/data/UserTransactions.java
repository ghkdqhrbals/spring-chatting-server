package com.example.shopuserservice.domain.user.data;

import com.example.commondto.events.user.UserStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@ToString
@RedisHash(value = "UserTransaction", timeToLive = 600)
public class UserTransactions implements Serializable {
    @Id
    private UUID eventId;
    private String userId;
    @Enumerated(EnumType.STRING)
    private UserStatus userStatus;
    @Enumerated(EnumType.STRING)
    private UserStatus chatStatus;
    @Enumerated(EnumType.STRING)
    private UserStatus customerStatus;
    private String userStatusMessage;
    private String chatStatusMessage;
    private String customerStatusMessage;
    private LocalDateTime createdAt;
    private String email;
    private String userName;
    private String userPw;
    private String role;

    @Builder
    public UserTransactions(UUID eventId, String userId, UserStatus userStatus, UserStatus chatStatus, UserStatus customerStatus, String userStatusMessage, String chatStatusMessage, String customerStatusMessage, LocalDateTime createdAt, String email, String userName, String userPw, String role) {
        this.eventId = eventId;
        this.userId = userId;
        this.userStatus = userStatus;
        this.chatStatus = chatStatus;
        this.customerStatus = customerStatus;
        this.userStatusMessage = userStatusMessage;
        this.chatStatusMessage = chatStatusMessage;
        this.customerStatusMessage = customerStatusMessage;
        this.createdAt = createdAt;
        this.email = email;
        this.userName = userName;
        this.userPw = userPw;
        this.role = role;
    }


    public UserTransactions() {
    }

}
