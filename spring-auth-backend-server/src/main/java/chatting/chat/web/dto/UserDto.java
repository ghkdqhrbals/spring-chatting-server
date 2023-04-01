package chatting.chat.web.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserDto {
    private String userId;
    private String userPw;
    private String userName;
    private String email;
    private LocalDateTime joinDate;
    private LocalDateTime loginDate;
    private LocalDateTime logoutDate;

    private String encryptedPw;

}
