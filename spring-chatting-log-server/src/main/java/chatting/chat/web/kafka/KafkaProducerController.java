//package chatting.chat.web.kafka;
//
//import chatting.chat.web.kafka.dto.RequestLoginDTO;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.ResponseEntity;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.kafka.support.SendResult;
//import org.springframework.util.concurrent.ListenableFuture;
//import org.springframework.util.concurrent.ListenableFutureCallback;
//import org.springframework.web.bind.annotation.*;
//
//@Slf4j
//@RestController
//@RequestMapping("/api")
//public class KafkaProducerController extends KafkaTopicConst{
//    private final KafkaTemplate<String, Object> kafkaProducerTemplate;
//
//    public KafkaProducerController(KafkaTemplate<String, Object> kafkaProducerTemplate) {
//        this.kafkaProducerTemplate = kafkaProducerTemplate;
//    }
//
//    @PostMapping("login")
//    public ResponseEntity<?> login(@RequestBody RequestLoginDTO requestLoginDTO) {
//
//        ListenableFuture<SendResult<String, Object>> future = kafkaProducerTemplate.send(TOPIC_LOGIN_REQUEST, requestLoginDTO.getUserId(), requestLoginDTO);
//
//        future.addCallback(new ListenableFutureCallback<SendResult<String, Object>>() {
//            @Override
//            public void onFailure(Throwable ex) {
//                log.error("Unable to send message: {}", ex.getMessage());
//            }
//            @Override
//            public void onSuccess(SendResult<String, Object> result) {
//                log.info("Sent message with topic: {}, key: {}, offset: {}, partition: {}",TOPIC_LOGIN_REQUEST, requestLoginDTO.getUserId(), result.getRecordMetadata().offset(), result.getRecordMetadata().partition());
//            }
//        });
//
//        return ResponseEntity.ok(requestLoginDTO);
//    }
//
//    @PostMapping("logout")
//    public ResponseEntity<?> logout(@RequestParam("userId") String userId) {
//
//        ListenableFuture<SendResult<String, Object>> future = kafkaProducerTemplate.send(TOPIC_LOGOUT_REQUEST, userId, userId);
//
//        future.addCallback(new ListenableFutureCallback<SendResult<String, Object>>() {
//            @Override
//            public void onFailure(Throwable ex) {
//                log.error("Unable to send message: {}", ex.getMessage());
//            }
//            @Override
//            public void onSuccess(SendResult<String, Object> result) {
//                log.info("Sent message with topic: {}, key: {}, offset: {}, partition: {}",TOPIC_LOGOUT_REQUEST, userId, result.getRecordMetadata().offset(), result.getRecordMetadata().partition());
//            }
//        });
//
//        return ResponseEntity.ok(userId);
//    }
//
//}