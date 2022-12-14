package chatting.chat.domain.data;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@Setter
public class Chatting {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ROOM_ID")
    private Room room;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User sendUser;

    @Column(name = "MESSAGE")
    private String message;

    @Column(name = "CREATED_DATE")
    private LocalDate createdDate;

    @Column(name = "CREATED_TIME")
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
