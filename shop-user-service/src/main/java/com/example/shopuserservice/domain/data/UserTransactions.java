package com.example.shopuserservice.domain.data;

import com.example.commondto.events.user.UserResponseStatus;
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
    private String userStatus;
    private String chatStatus;
    private String customerStatus;
    private LocalDateTime createdAt;
    private String email;
    private String userName;
    private String userPw;
    private String role;

    @Builder
    public UserTransactions(UUID eventId, UserStatus userStatus,
                            UserResponseStatus u1,
                            UserResponseStatus u2,
                            String userId,
                            LocalDateTime createdAt,
                            String email,
                            String userName,
                            String userPw,
                            String role) {
        this.eventId = eventId;
        this.userStatus = userStatus.name();
        this.chatStatus = u1.name();
        this.customerStatus = u2.name();
        this.userId = userId;
        this.createdAt = createdAt;
        this.email = email;
        this.userName = userName;
        this.userPw = userPw;
        this.role = role;
    }

    public UserTransactions() {
    }

}
