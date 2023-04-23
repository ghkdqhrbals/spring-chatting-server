package com.example.shopuserservice.web.kafka;



import com.example.commondto.kafka.KafkaTopic;
import com.example.commondto.events.user.UserResponseEvent;
import com.example.commondto.kafka.KafkaTopicPartition;
import com.example.shopuserservice.config.AsyncConfig;
import com.example.shopuserservice.domain.user.service.UserCommandQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageListener {
    private final UserCommandQueryService userService;
    private final KafkaTemplate<String, Object> kafkaProducerTemplate;

    // concurrency를 partition 개수에 맞추어 설정하는 것이 중요합니다.
    @KafkaListener(topics = KafkaTopic.userRes, containerFactory = "userKafkaListenerContainerFactory", concurrency = KafkaTopicPartition.userRes)
    public void listenUser(UserResponseEvent req) {
        log.info("메세지 도착 = {}", req.getServiceName());

        userService.updateStatus(req).exceptionally(e->{
            AsyncConfig.sinkMap.get(req.getUserId()).tryEmitError(e);
            return null;
        });
    }

    private void sendToKafka(String topic,Object req) {
        kafkaProducerTemplate.send(topic, req).thenAccept((SendResult<String, Object> result)->{
            log.debug("메세지 전송 성공 topic={}, offset={}, partition={}",topic, result.getRecordMetadata().offset(), result.getRecordMetadata().partition());
        }).exceptionally(e->{
            log.error("메세지 전송 실패={}", e.getMessage());
            return null;
        });
    }
}