package chatting.chat.web.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class UserStatusUpdateDTO {

    String statusMessage;

    public UserStatusUpdateDTO(String statusMessage) {
        this.statusMessage = statusMessage;
    }
}
