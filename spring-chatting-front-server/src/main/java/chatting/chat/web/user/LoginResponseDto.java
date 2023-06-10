package chatting.chat.web.user;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class LoginResponseDto {
    private String token;

    public LoginResponseDto() {
    }

    public LoginResponseDto(String token) {
        this.token = token;
    }
}
