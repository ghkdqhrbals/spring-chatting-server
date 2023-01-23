package chatting.chat.domain.data;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class Chatting {
    private Long id;
    private Room room;
    private User sendUser;
    private String message;
    private LocalDate createdDate;
    private LocalTime createdTime;

    public Chatting(Long id, Room room, User sendUser, String message, LocalDate createdDate, LocalTime createdTime) {
        this.id = id;
        this.room = room;
        this.sendUser = sendUser;
        this.message = message;
        this.createdDate = createdDate;
        this.createdTime = createdTime;
    }


    public Chatting() {

    }
}
