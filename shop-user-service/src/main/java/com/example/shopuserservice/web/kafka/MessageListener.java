package com.example.shopuserservice.web.kafka;



import com.example.commondto.dto.RequestUserChangeDto;
import com.example.commondto.dto.ResponseUserChangeDto;
import com.example.commondto.events.topic.KafkaTopic;
import com.example.commondto.events.user.UserEvent;
import com.example.commondto.events.user.UserResponseEvent;
import com.example.commondto.events.user.UserResponseStatus;
import com.example.commondto.events.user.UserStatus;
import com.example.shopuserservice.domain.user.service.UserCommandQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Sinks;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageListener {
    private final UserCommandQueryService userService;
    private final KafkaTemplate<String, Object> kafkaProducerTemplate;

    // concurrency를 partition 개수에 맞추어 설정하는 것이 중요합니다.
    @KafkaListener(topics = KafkaTopic.user_add_res, containerFactory = "userKafkaListenerContainerFactory", concurrency = "2")
    public void listenUser(UserResponseEvent req) {
        System.out.println("THREAD:"+Thread.currentThread().getName()+" STATUS:"+req.getUserResponseStatus()+" FROM:"+req.getServiceName());
        ResponseUserChangeDto res = req.getResponseUserChangeDto();
        try {
            userService.updateStatus(req);
        }catch(Exception e){}

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