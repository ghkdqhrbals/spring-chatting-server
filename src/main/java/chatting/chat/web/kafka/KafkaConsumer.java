package chatting.chat.web.kafka;

import chatting.chat.web.dto.ChatMessageDTO;
import chatting.chat.web.dto.ChattingDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumer {

    private final SimpMessagingTemplate template;

    @KafkaListener(topics = KafkaConsts.KAFKA_TOPIC, groupId = KafkaConsts.GROUP_ID)
    public void consume(ChatMessageDTO message) throws IOException {
        log.info("KafkaConsumer.consumeMessage");
        log.info("KafkaConsumer.messageContent : roomId={}, writer={}, message={}: ", message.getRoomId(), message.getWriter(), message.getMessage());
        HashMap<String, String> msg = new HashMap<>();
        msg.put("roomName", String.valueOf(message.getRoomId()));
        msg.put("message", message.getMessage());
        msg.put("writer", message.getWriter());

        template.convertAndSend("/sub/chat/room/" + message.getRoomId(), message); // stomp topic send
    }
}