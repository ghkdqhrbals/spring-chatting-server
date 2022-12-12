package chatting.chat.domain.data;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

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

    @Column(name = "CREATED_AT")
    private LocalDate createdAt;

    @Column(name = "UPDATED_AT")
    private LocalDate UpdatedAt;

    public Chatting(Room room, User sendUser, String message, LocalDate createdAt, LocalDate updatedAt) {
        this.room = room;
        this.sendUser = sendUser;
        this.message = message;
        this.createdAt = createdAt;
        UpdatedAt = updatedAt;
    }

    public Chatting() {

    }
}
