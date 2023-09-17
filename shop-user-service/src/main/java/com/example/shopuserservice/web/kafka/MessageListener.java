package com.example.shopuserservice.web.kafka;

import com.example.commondto.kafka.KafkaTopic;
import com.example.commondto.events.user.UserResponseEvent;
import com.example.commondto.kafka.KafkaTopicPartition;
import com.example.shopuserservice.domain.user.service.UserCommandQueryService;
import com.example.shopuserservice.web.util.reactor.Reactor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@ConditionalOnProperty(value = "kafka.enabled", matchIfMissing = true)
@RequiredArgsConstructor
public class MessageListener {
    private final UserCommandQueryService userService;
    private final KafkaTemplate<String, Object> kafkaProducerTemplate;

    // concurrency를 partition 개수에 맞추어 설정하는 것이 중요합니다.
    @KafkaListener(topics = KafkaTopic.userRes, containerFactory = "userKafkaListenerContainerFactory", concurrency = KafkaTopicPartition.userRes)
    public void listenUser(UserResponseEvent req) {
        userService.updateStatus(req).exceptionally(e->{
            log.info("이벤트 트랜젝션이 발견되지 않았습니다");
            Reactor.emitErrorAndComplete(req.getUserId(), e);
            return null;
        });
    }

    private CompletableFuture<?> sendToKafkaWithKey(String topic, Object req, String key) {
        return kafkaProducerTemplate.send(topic,key, req).thenRun(()->{
            log.info("메세지 전송 성공");
        }).exceptionally(e->{
            log.error("메세지 전송 실패");
            return null;
        });
    }
}