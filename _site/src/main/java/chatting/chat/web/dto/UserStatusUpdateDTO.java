package chatting.chat.web.dto;

import lombok.Data;

@Data
public class UserStatusUpdateDTO {
    String statusMessage;

    public UserStatusUpdateDTO(String statusMessage) {
        this.statusMessage = statusMessage;
    }
}
