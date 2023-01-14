package chatting.chat.web.kafka.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class RequestAddChatRoomDTO {
    private String userId;
    private List<String> friendIds;
}
