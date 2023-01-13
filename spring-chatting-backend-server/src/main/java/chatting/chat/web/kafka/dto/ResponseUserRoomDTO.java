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
}
