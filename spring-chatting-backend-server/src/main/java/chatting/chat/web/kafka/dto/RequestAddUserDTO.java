package chatting.chat.web.kafka.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestAddUserDTO {
    private String userId;
    private String userName;
}
