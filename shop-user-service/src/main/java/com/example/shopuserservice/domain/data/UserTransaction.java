package com.example.shopuserservice.domain.data;

import com.example.commondto.events.user.UserResponseStatus;
import com.example.commondto.events.user.UserStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Data
@Entity
@Table(name = "USER_TRANSACTION_TABLE")
public class UserTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;


    @Column(name = "EVENT_ID", unique = true)
    private UUID eventId;
    private String userId;
    private String userStatus;
    private String chatStatus;
    private String customerStatus;
    private LocalDateTime createdAt;

    public UserTransaction(UUID eventId, UserStatus userStatus, UserResponseStatus u1, UserResponseStatus u2, String userId, LocalDateTime createdAt) {
        this.eventId = eventId;
        this.userStatus = userStatus.name();
        this.chatStatus = u1.name();
        this.customerStatus = u2.name();
        this.userId = userId;
        this.createdAt = createdAt;
    }

    public UserTransaction() {
    }

}
