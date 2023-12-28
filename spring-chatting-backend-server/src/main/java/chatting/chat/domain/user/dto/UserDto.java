package chatting.chat.domain.user.dto;

import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
public class UserDto {
    private String userId;
    private String userName;
    private String userStatus;

    @Builder
    public UserDto(String userId, String userName, String userStatus) {
        this.userId = userId;
        this.userName = userName;
        this.userStatus = userStatus;
    }

}
