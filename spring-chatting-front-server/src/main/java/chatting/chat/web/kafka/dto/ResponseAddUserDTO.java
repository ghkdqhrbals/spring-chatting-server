package chatting.chat.web.kafka.dto;

import chatting.chat.domain.data.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseAddUserDTO {
    private String userId;
    private Boolean isSuccess;
    private User user;

    public ResponseAddUserDTO(String userId) {
        this.userId = userId;
        this.isSuccess = false;
        this.user=new User();
    }

    public ResponseAddUserDTO() {
    }
}
