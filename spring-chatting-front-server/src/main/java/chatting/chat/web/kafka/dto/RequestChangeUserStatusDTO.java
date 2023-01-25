package chatting.chat.web.kafka.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestChangeUserStatusDTO {
    private String userId;
    private String status;

    public RequestChangeUserStatusDTO(String userId, String status) {
        this.userId = userId;
        this.status = status;
    }
}
