package com.example.orderservice.kafka;
import com.example.commondto.events.ServiceNames;
import com.example.commondto.kafka.KafkaTopic;
import com.example.commondto.events.user.UserEvent;
import com.example.commondto.events.user.UserResponseEvent;
import com.example.commondto.events.user.UserResponseStatus;
import com.example.commondto.events.user.UserStatus;
import com.example.commondto.kafka.KafkaTopicPartition;
import com.example.orderservice.service.UserBalanceCommandQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageListener {
    private final UserBalanceCommandQueryService userService;

    private final KafkaTemplate<String, Object> kafkaProducerTemplate;

    @KafkaListener(topics = KafkaTopic.userReq, containerFactory = "userKafkaListenerContainerFactory", concurrency = KafkaTopicPartition.userReq)
    public void listenUserRemove(UserEvent req) {
        System.out.println("THREAD:"+Thread.currentThread().getName()+" STATUS:"+req.getUserStatus()+" ID:"+req.getUserId());
        if (req.getUserStatus().equals(UserStatus.USER_INSERT.name())) {
            userService.saveUserBalance(req.getUserId(), req.getEventId())
                    .thenRun(()->{
                        sendToKafka(KafkaTopic.userRes,
                                new UserResponseEvent(req.getEventId(),
                                    req.getUserId(),
                                    UserResponseStatus.USER_SUCCES.name(),
                                    ServiceNames.customer));
            }).exceptionally(e->{
                log.info("Exception={}",e.getMessage());
                sendToKafka(KafkaTopic.userRes,
                        new UserResponseEvent(req.getEventId(),
                                req.getUserId(),
                                UserResponseStatus.USER_FAIL.name(),
                                ServiceNames.customer));
                log.info("saveUser={}",e.getMessage());
                return null;
            });
        } else if (req.getUserStatus().equals(UserStatus.USER_DELETE.name())) {
            userService.removeUserBalance(req.getUserId())
                    .thenRun(()->{
                        sendToKafka(KafkaTopic.userRes,
                                new UserResponseEvent(req.getEventId(),
                                        req.getUserId(),
                                        UserResponseStatus.USER_SUCCES.name(),
                                        ServiceNames.customer));
                    })
                    .exceptionally(e->{
                        sendToKafka(KafkaTopic.userRes,
                                new UserResponseEvent(req.getEventId(),
                                        req.getUserId(),
                                        UserResponseStatus.USER_FAIL.name(),
                                        ServiceNames.customer));
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




    private void sendToKafka(String topic,Object req) {
        kafkaProducerTemplate.send(topic, req).thenAccept((SendResult<String, Object> result)->{
            log.info("메세지 전송 성공 topic={}, offset={}, partition={}",topic, result.getRecordMetadata().offset(), result.getRecordMetadata().partition());
        }).exceptionally(e->{
            log.error("메세지 전송 실패={}", e.getMessage());
            return null;
        });
    }
}