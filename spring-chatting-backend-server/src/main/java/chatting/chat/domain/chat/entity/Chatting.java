package chatting.chat.domain.chat.entity;

import chatting.chat.domain.room.entity.Room;
import chatting.chat.domain.user.entity.User;
import chatting.chat.entityDefault.BaseTime;
import com.example.commondto.dto.chat.ChatRequest;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Chatting extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "chatting_seq")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ROOM_ID")
    private Room room;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private User sendUser;
    @Column(name = "MESSAGE")
    private String message;

    @Builder
    public Chatting(Long id, Room room, User sendUser, String message, LocalDateTime createdAt) {
        this.id = id;
        this.room = room;
        this.sendUser = sendUser;
        this.message = message;
        this.createdAt = createdAt;
    }

    public Chatting() {

    }

    /**
     * {@link Chatting} 를 {@link ChatRequest.ChatRecordDTO} 로 변환
     * @return {@link ChatRequest.ChatRecordDTO}
     */
    public ChatRequest.ChatRecordDTO toChatRecordDTO() {
        return ChatRequest.ChatRecordDTO.builder()
                .id(String.valueOf(this.id))
                .roomId(this.room.getRoomId())
                .sendUserId(this.sendUser.getUserId())
                .sendUserName(this.sendUser.getUserName())
                .message(this.message)
                .createdAt(this.createdAt)
                .build();
    }
}
