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

import java.util.concurrent.CompletableFuture;

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

        userService.updateStatus2(req).exceptionally(e->{
            AsyncConfig.sinkMap.get(req.getUserId()).tryEmitError(e);
            sendToKafkaWithKey(KafkaTopic.userCustomerRollback, req.getUserId(), req.getUserId());
            sendToKafkaWithKey(KafkaTopic.userChatRollback, req.getUserId(), req.getUserId());
            return null;
        });
    }

    private CompletableFuture<?> sendToKafkaWithKey(String topic, Object req, String key) {
        return kafkaProducerTemplate.send(topic,key, req);
    }
}