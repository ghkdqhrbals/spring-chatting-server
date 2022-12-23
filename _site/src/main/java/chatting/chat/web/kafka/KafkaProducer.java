package chatting.chat.web.kafka;

import chatting.chat.web.dto.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducer {

    private final KafkaTemplate<String, ChatMessage> kafkaTemplate;

    public void send(String topic, ChatMessage messageDto) {
        log.info("KafkaProducer.sendTopic : " + topic);
        log.info("KafkaProducer.messageContent : roomId={}, writer={}, message={}: ", messageDto.getRoomId(), messageDto.getWriter(), messageDto.getMessage());
        kafkaTemplate.send(topic, messageDto);
    }
}