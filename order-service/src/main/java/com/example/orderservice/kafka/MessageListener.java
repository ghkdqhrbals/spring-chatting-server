package com.example.orderservice.kafka;


import chatting.chat.domain.user.service.UserService;
import com.example.commondto.events.order.OrderEvent;
import com.example.commondto.events.user.UserResponseEvent;
import com.example.commondto.kafka.KafkaTopic;
import com.example.commondto.kafka.KafkaTopicPartition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageListener {

    private final KafkaTemplate<String, Object> kafkaProducerTemplate;

    @KafkaListener(topics = KafkaTopic.productNewOrderRes, containerFactory = "userKafkaListenerContainerFactory", concurrency = KafkaTopicPartition.productNewOrderRes)
    public void listenProductNewOrderResponse(OrderEvent req) {

    }
    @KafkaListener(topics = KafkaTopic.customerNewOrderRes, containerFactory = "userKafkaListenerContainerFactory", concurrency = KafkaTopicPartition.customerNewOrderRes)
    public void listenCustomerNewOrderResponse(OrderEvent req) {

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