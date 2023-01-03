package chatting.chat.web.kafka.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
@Getter
@Setter
public class ResponseAddUserRoomDTO {
    private String userId;
    private Boolean isSuccess;
    private List<String> friendId;
    private ZonedDateTime createdAt;
    private String errorMessage;

    public ResponseAddUserRoomDTO(String userId) {
        this.userId = userId;
        this.errorMessage="";
    }

    public ResponseAddUserRoomDTO() {
        this.isSuccess=false;
        this.friendId=new ArrayList<>();
        this.errorMessage="";
    }
}
