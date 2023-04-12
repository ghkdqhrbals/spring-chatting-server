package chatting.chat.web.kafka;


import chatting.chat.domain.chat.ChatService;
import chatting.chat.domain.data.Chatting;
import chatting.chat.domain.data.Friend;
import chatting.chat.domain.data.Room;
import chatting.chat.domain.data.User;
import chatting.chat.domain.friend.service.FriendService;
import chatting.chat.domain.room.service.RoomService;
import chatting.chat.domain.user.service.UserService;
import chatting.chat.web.kafka.dto.*;
import com.example.commondto.dto.RequestUserChangeDto;
import com.example.commondto.dto.ResponseUserChangeDto;
import com.example.commondto.events.ServiceNames;
import com.example.commondto.events.topic.KafkaTopic;
import com.example.commondto.events.user.UserEvent;
import com.example.commondto.events.user.UserResponseEvent;
import com.example.commondto.events.user.UserResponseStatus;
import com.example.commondto.events.user.UserStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageListener {
    private final UserService userService;
    private final KafkaTemplate<String, Object> kafkaProducerTemplate;

    // concurrency를 partition 개수에 맞추어 설정하는 것이 중요합니다.
    @KafkaListener(topics = KafkaTopic.user_add_req, containerFactory = "userKafkaListenerContainerFactory", concurrency = "2")
    public void listenUser(UserEvent req) {
        System.out.println("THREAD:"+Thread.currentThread().getName()+" STATUS:"+req.getUserStatus()+" ID:"+req.getUserDto().getUserId());
        RequestUserChangeDto userDto = req.getUserDto();

        try {
            if (req.getUserStatus().equals(UserStatus.USER_INSERT.name())) {
                userService.save(userDto.getUserId(), userDto.getUserId(), "");
            } else if (req.getUserStatus().equals(UserStatus.USER_DELETE.name())) {
                userService.remove(userDto.getUserId());
            }
            sendToKafka(KafkaTopic.user_add_res,
                    new UserResponseEvent(req.getEventId(), new ResponseUserChangeDto(userDto.getUserId(), userDto.getUserName()),
                            UserResponseStatus.USER_SUCCES.name(), ServiceNames.chat));
        }catch(Exception e){
            sendToKafka(KafkaTopic.user_add_res,
                    new UserResponseEvent(req.getEventId(), new ResponseUserChangeDto(userDto.getUserId(), userDto.getUserName()),
                            UserResponseStatus.USER_FAIL.name(),ServiceNames.chat));
        }
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