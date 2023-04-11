package com.example.productservice.kafka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class KafkaTopicConst {
    @Value("${kafka.topic-user-add-request}")
    public String TOPIC_USER_ADD_REQUEST;
    @Value("${kafka.topic-user-add-chat-request}")
    public String TOPIC_USER_ADD_CHAT_REQUEST;

    @Value("${kafka.topic-user-change}")
    public String TOPIC_USER_CHANGE;

}
