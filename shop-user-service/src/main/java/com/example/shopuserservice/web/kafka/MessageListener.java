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
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(value = "kafka.enabled", matchIfMissing = true)
@RequiredArgsConstructor
public class MessageListener {
    private final UserCommandQueryService userService;
    // it is important to set concurrency according to the number of partitions
    @KafkaListener(topics = KafkaTopic.userRes, containerFactory = "userKafkaListenerContainerFactory", concurrency = KafkaTopicPartition.userRes)
    public void listenUser(UserResponseEvent req) {
        userService.updateStatus(req).exceptionally(e->{
            log.info("Event transaction is failed.");
            Reactor.emitErrorAndComplete(req.getUserId(), e);
            return null;
        });
    }
}