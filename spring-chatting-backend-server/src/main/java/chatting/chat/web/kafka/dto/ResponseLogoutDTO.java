package chatting.chat.web.kafka.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseLogoutDTO {
    private String userId;
    private Boolean isSuccess;
    private String errorMessage;
}
