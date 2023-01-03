package chatting.chat.web.kafka.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ResponseUserRoomDTO {
    private String userId;
    private List<ChatRoomDTO> chatRoomDTOS;

    public ResponseUserRoomDTO(String userId) {
        this.userId = userId;
        this.chatRoomDTOS=new ArrayList<>();
    }

    public ResponseUserRoomDTO(String userId, Boolean isSuccess, List<ChatRoomDTO> chatRoomDTOS, String errorMessage) {
        this.userId = userId;
        this.chatRoomDTOS = chatRoomDTOS;
    }

    public ResponseUserRoomDTO(String userId, Boolean isSuccess, String errorMessage) {
        this.userId = userId;
        this.chatRoomDTOS = new ArrayList<>();
    }
}
