package chatting.chat.web.login.dto;

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
