package chatting.chat.domain.data;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
public class Participant {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long participantId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="ROOM_ID")
    private Room room;

    @Column(name = "ROOM_NAME")
    private String roomName;

    @Column(name = "CREATED_AT")
    private LocalDate createdAt;

    @Column(name = "UPDATED_AT")
    private LocalDate UpdatedAt;

    public Participant(User user, Room room, String roomName, LocalDate createdAt, LocalDate updatedAt) {
        this.user = user;
        this.room = room;
        this.roomName = roomName;
        this.createdAt = createdAt;
        this.UpdatedAt = updatedAt;
    }

    public Participant() {

    }
}
