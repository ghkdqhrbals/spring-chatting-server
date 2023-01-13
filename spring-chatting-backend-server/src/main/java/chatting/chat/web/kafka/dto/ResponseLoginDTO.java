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
}
