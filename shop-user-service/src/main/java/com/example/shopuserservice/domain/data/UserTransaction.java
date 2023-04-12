package com.example.shopuserservice.domain.data;

import com.example.commondto.events.user.UserResponseStatus;
import com.example.commondto.events.user.UserStatus;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "USER_TRANSACTION_TABLE")
public class UserTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;


    @Column(name = "EVENT_ID", unique = true)
    private String eventId;
    private String userStatus;
    private String chatStatus;
    private String customerStatus;

    public UserTransaction(String eventId, UserStatus userStatus, UserResponseStatus u1, UserResponseStatus u2) {
        this.eventId = eventId;
        this.userStatus = userStatus.name();
        this.chatStatus = u1.name();
        this.customerStatus = u2.name();
    }

    public UserTransaction() {
    }

}
