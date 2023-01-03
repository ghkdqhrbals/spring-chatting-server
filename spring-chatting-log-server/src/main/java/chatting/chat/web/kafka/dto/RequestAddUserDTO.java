package chatting.chat.web.kafka.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class RequestAddUserDTO {
    private String userId;
    private String userName;
}
