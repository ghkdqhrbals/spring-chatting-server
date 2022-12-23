package chatting.chat.web.kafka.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestLoginDTO {
    private String userId;
    private String userPw;
}
