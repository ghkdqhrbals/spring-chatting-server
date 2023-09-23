package com.example.orderservice.kafka;
import com.example.commondto.events.ServiceNames;
import com.example.commondto.kafka.KafkaTopic;
import com.example.commondto.events.user.UserEvent;
import com.example.commondto.events.user.UserResponseEvent;
import com.example.commondto.events.user.UserStatus;
import com.example.commondto.kafka.KafkaTopicPartition;
import com.example.orderservice.service.UserBalanceCommandQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageListener {
    private final UserBalanceCommandQueryService userService;

    private final KafkaTemplate<String, Object> kafkaProducerTemplate;

    @KafkaListener(topics = KafkaTopic.userReq, containerFactory = "userKafkaListenerContainerFactory", concurrency = KafkaTopicPartition.userReq)
    public void listenUser(UserEvent req) {
        if (req.getUserStatus().equals(UserStatus.USER_INSERT_APPEND.name())) {
            userService.saveUserBalance(req.getUserId(), req.getEventId())
                    .thenRun(()->{
                        sendToKafkaWithKey(KafkaTopic.userRes,
                                new UserResponseEvent(req.getEventId(),
                                    req.getUserId(),
                                    UserStatus.USER_INSERT_SUCCESS.name(),
                                    ServiceNames.customer,""), req.getEventId().toString());
            }).exceptionally(e->{
                sendToKafkaWithKey(KafkaTopic.userRes,
                        new UserResponseEvent(req.getEventId(),
                                req.getUserId(),
                                UserStatus.USER_INSERT_FAIL.name(),
                                ServiceNames.customer,e.getMessage()), req.getEventId().toString());
                log.info("saveUser={}",e.getMessage());
                return null;
            });
        } else if (req.getUserStatus().equals(UserStatus.USER_DELETE_APPEND.name())) {
            userService.removeUserBalance(req.getUserId())
                    .thenRun(()->{
                        sendToKafkaWithKey(KafkaTopic.userRes,
                                new UserResponseEvent(req.getEventId(),
                                        req.getUserId(),
                                        UserStatus.USER_DELETE_SUCCESS.name(),
                                        ServiceNames.customer,""),req.getEventId().toString());
                    })
                    .exceptionally(e->{
                        sendToKafkaWithKey(KafkaTopic.userRes,
                                new UserResponseEvent(req.getEventId(),
                                        req.getUserId(),
                                        UserStatus.USER_DELETE_FAIL.name(),
                                        ServiceNames.customer,e.getMessage()),req.getEventId().toString());
                        return null;
                    });;
        }
    }

    @KafkaListener(topics = KafkaTopic.userCustomerRollback, containerFactory = "userRollbackKafkaListenerContainerFactory", concurrency = KafkaTopicPartition.userCustomerRollback)
    public void listenUserRollback(String userId) {
        try{
            userService.removeUserBalance(userId);
        }catch(Exception e){
            log.info(e.getMessage());
        }
    }

    private CompletableFuture<?> sendToKafkaWithKey(String topic, Object req, String key) {
        return kafkaProducerTemplate.send(topic,key, req);
    }



    private void sendToKafka(String topic,Object req) {
        kafkaProducerTemplate.send(topic, req).thenAccept((SendResult<String, Object> result)->{
            log.info("메세지 전송 성공 topic={}, offset={}, partition={}",topic, result.getRecordMetadata().offset(), result.getRecordMetadata().partition());
        }).exceptionally(e->{
            log.error("메세지 전송 실패={}", e.getMessage());
            return null;
        });
    }
}