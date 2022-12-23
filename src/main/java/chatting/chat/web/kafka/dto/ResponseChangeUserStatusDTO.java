package chatting.chat.web.kafka.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseChangeUserStatusDTO {
    private String userId;
    private Boolean isSuccess;
    private String status;
    private String errorMessage;

    public ResponseChangeUserStatusDTO(String userId) {
        this.userId = userId;
        this.isSuccess=false;
    }

    public ResponseChangeUserStatusDTO() {
    }
}
