package chatting.chat.web.kafka.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseLogoutDTO {
    private String userId;
    private Boolean isSuccess;
    private String errorMessage;

    public ResponseLogoutDTO(String userId, Boolean isSuccess, String errorMessage) {
        this.userId = userId;
        this.isSuccess = isSuccess;
        this.errorMessage = errorMessage;
    }
}
