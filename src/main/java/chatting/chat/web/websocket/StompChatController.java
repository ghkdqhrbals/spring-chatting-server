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
    private final SimpMessagingTemplate template; // 특정 Broker로 메세지를 전달
    private final KafkaProducer kafkaProducer; // Redirect send to kafka Producer that we already created

    @MessageMapping(value = "/chat/enter") // "/pub/chat/enter"
    public void enter(ChatMessage message){
        message.setMessage(message.getWriter() + "님이 채팅방에 참여하였습니다.");
        template.convertAndSend("/sub/chat/room/" + message.getRoomId(), message); // Direct send topic to stomp
    }

    @MessageMapping(value = "/chat/message") //"/pub/chat/message"
    public void message(ChatMessage message){
        log.info(message.getMessage());
        kafkaProducer.send(KafkaConsts.KAFKA_TOPIC, message); // kafka topic send
    }
}