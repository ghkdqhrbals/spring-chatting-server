package chatting.chat.web.kafka;

import chatting.chat.domain.chat.ChatService;
import chatting.chat.domain.data.Chatting;
import chatting.chat.domain.data.Room;
import chatting.chat.domain.data.User;
import chatting.chat.domain.room.service.RoomService;
import chatting.chat.domain.room.service.RoomServiceImpl;
import chatting.chat.domain.user.service.UserService;
import chatting.chat.domain.user.service.UserServiceImpl;
import chatting.chat.web.dto.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumer {
    private final SimpMessagingTemplate template;
    private final ChatService chatService;
    private final UserService userService;
    private final RoomService roomService;

    @KafkaListener(topics = KafkaConsts.KAFKA_TOPIC, groupId = KafkaConsts.GROUP_ID)
    public void consume(ChatMessage message) throws IOException {
        log.info("KafkaConsumer.consumeMessage");
        log.info("KafkaConsumer.messageContent : roomId={}, writer={}, message={}, time={}: ", message.getRoomId(), message.getWriter(), message.getMessage(),message.getCreateAt());
        HashMap<String, String> msg = new HashMap<>();
        msg.put("roomName", String.valueOf(message.getRoomId()));
        msg.put("message", message.getMessage());
        msg.put("writer", message.getWriter());
        msg.put("createAt", message.getCreateAt().toString());
        Optional<Room> room = roomService.findByRoomId(message.getRoomId());
        User user = userService.findByUserId(message.getWriterId());

        log.info("room={}",room.get().getRoomId());
        log.info("user={}",user.getUserName());

        Chatting chatting = message.convertToChatting(room.get(), user);
        chatService.save(chatting);

        template.convertAndSend("/sub/chat/room/" + message.getRoomId(), message); // stomp topic send
    }
}