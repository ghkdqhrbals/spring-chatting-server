package chatting.chat.web.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class AddUserResponse {

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

    public AddUserResponse(UUID eventId, String userId, String userStatus, String chatStatus, String customerStatus, LocalDateTime createdAt, String email, String userName, String userPw, String role) {
        this.eventId = eventId;
        this.userId = userId;
        this.userStatus = userStatus;
        this.chatStatus = chatStatus;
        this.customerStatus = customerStatus;
        this.createdAt = createdAt;
        this.email = email;
        this.userName = userName;
        this.userPw = userPw;
        this.role = role;
    }

    public AddUserResponse() {
    }

}
