package chatting.chat.web.kafka;


import chatting.chat.domain.user.service.UserService;
import com.example.commondto.events.ServiceNames;
import com.example.commondto.kafka.KafkaTopic;
import com.example.commondto.events.user.UserEvent;
import com.example.commondto.events.user.UserResponseEvent;
import com.example.commondto.events.user.UserStatus;
import com.example.commondto.kafka.KafkaTopicPartition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("prod")
@RequiredArgsConstructor
public class MessageListener {
    private final UserService userService;
    private final KafkaTemplate<String, Object> kafkaProducerTemplate;

    @KafkaListener(topics = KafkaTopic.userReq, containerFactory = "userKafkaListenerContainerFactory", concurrency = KafkaTopicPartition.userReq)
    public void listenUserRemove(UserEvent req) {
        System.out.println("THREAD:"+Thread.currentThread().getName()+" STATUS:"+req.getUserStatus()+" ID:"+req.getUserId());

        try {
            if (req.getUserStatus().equals(UserStatus.USER_INSERT_APPEND.name())) {
                userService.save(req.getUserId(), req.getUserId(), "");
            } else if (req.getUserStatus().equals(UserStatus.USER_DELETE_APPEND.name())) {
                userService.remove(req.getUserId());
            }
            sendToKafkaWithKey(KafkaTopic.userRes,
                    new UserResponseEvent(req.getEventId(),
                            req.getUserId(),
                            UserStatus.USER_INSERT_SUCCESS.name(),
                            ServiceNames.chat,""),req.getEventId().toString());
        }catch(Exception e){
            log.info(e.getMessage());
            sendToKafkaWithKey(KafkaTopic.userRes,
                    new UserResponseEvent(req.getEventId(),
                            req.getUserId(),
                            UserStatus.USER_INSERT_SUCCESS.name(),
                            ServiceNames.chat,""),req.getEventId().toString());
        }
    }

    @KafkaListener(topics = KafkaTopic.userChatRollback, containerFactory = "userRollbackKafkaListenerContainerFactory", concurrency = KafkaTopicPartition.userChatRollback)
    public void listenUserRemove(String userId) {
        try {
            userService.removeUser(userId);
        }catch(Exception e){
            log.info(e.getMessage());
        }
    }

    private void sendToKafkaWithKey(String topic, Object req, String key) {
        kafkaProducerTemplate.send(topic, key, req);
    }
}