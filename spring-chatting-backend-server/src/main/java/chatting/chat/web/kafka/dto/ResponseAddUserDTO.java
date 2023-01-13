package chatting.chat.web.kafka.dto;

import chatting.chat.domain.data.User;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
public class ResponseAddUserDTO {
    private String userId;
    private Boolean isSuccess;
    private User user;
    private String errorMessage;
}
