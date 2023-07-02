package chatting.chat.web.kafka.dto;

import chatting.chat.domain.data.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseLoginDTO {
    private String userId;
    private Boolean isSuccess;
    private String errorMessage;
    private User user;

    public ResponseLoginDTO(String userId) {
        this.userId = userId;
        this.isSuccess=false;
    }

    public ResponseLoginDTO(String userId, Boolean isSuccess, String errorMessage, User user) {
        this.userId = userId;
        this.isSuccess = isSuccess;
        this.errorMessage = errorMessage;
        this.user = user;
    }
}
