package chatting.chat.web.kafka.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ResponseUserRoomDTO {
    private String userId;
    private Boolean isSuccess;
    private List<ChatRoomDTO> chatRoomDTOS;
    private String errorMessage;

    public ResponseUserRoomDTO(String userId) {
        this.userId = userId;
        this.isSuccess=false;
        this.chatRoomDTOS=new ArrayList<>();
    }

    public ResponseUserRoomDTO(String userId, Boolean isSuccess, List<ChatRoomDTO> chatRoomDTOS, String errorMessage) {
        this.userId = userId;
        this.isSuccess = isSuccess;
        this.chatRoomDTOS = chatRoomDTOS;
        this.errorMessage = errorMessage;
    }

    public ResponseUserRoomDTO(String userId, Boolean isSuccess, String errorMessage) {
        this.userId = userId;
        this.isSuccess = isSuccess;
        this.chatRoomDTOS = new ArrayList<>();
        this.errorMessage = errorMessage;
    }
}
