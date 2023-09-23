package com.example.shopuserservice.web.kafka;

import com.example.commondto.events.user.UserResponseEvent;
import com.example.shopuserservice.web.util.kafka.KafkaConsumerManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;

@Slf4j
@EnableKafka
@ConditionalOnProperty(value = "kafka.enabled", matchIfMissing = true)
@Configuration
public class KafkaConsumerConfig {
    private final KafkaConsumerManager kafkaConsumerManager;

    public KafkaConsumerConfig(KafkaConsumerManager kafkaConsumerManager) {
        this.kafkaConsumerManager = kafkaConsumerManager;
    }

    // Response of registering user event listener
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, UserResponseEvent> userKafkaListenerContainerFactory() {
        return kafkaConsumerManager.getContainerFactory("user", UserResponseEvent.class);
    }
}