package chatting.chat.web.websocket;

import chatting.chat.web.dto.ChatMessage;
import chatting.chat.web.kafka.KafkaConsts;
import chatting.chat.web.kafka.KafkaProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class StompChatController {

    // STOMP 템플릿
    private final SimpMessagingTemplate template;

    // Redirect 용 kafka Producer
    private final KafkaProducer kafkaProducer;

    // "/pub/chat/enter"
    @MessageMapping(value = "/chat/enter")
    public void enter(ChatMessage message){
        message.setMessage(message.getWriter() + "님이 채팅방에 참여하였습니다.");
        template.convertAndSend("/sub/chat/room/" + message.getRoomId(), message); // Direct send topic to stomp
    }

    //"/pub/chat/message"
    @MessageMapping(value = "/chat/message")
    public void message(ChatMessage message){
        log.info(message.getMessage());
        // kafka topic send
        kafkaProducer.send(KafkaConsts.KAFKA_TOPIC, message);
    }
}