package com.example.shopuserservice.web.kafka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class KafkaTopicConst {
    @Value("${kafka.topic-user-change}")
    public String TOPIC_USER_CHANGE;

}
