package com.example.shopuserservice.web.kafka;

import com.example.commondto.events.user.UserResponseEvent;
import com.example.shopuserservice.web.util.kafka.ConsumerManager;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

@Slf4j
@EnableKafka
@ConditionalOnProperty(value = "kafka.enabled", matchIfMissing = true)
@Configuration
public class KafkaConsumerConfig {
    private final ConsumerManager consumerManager;

    public KafkaConsumerConfig(ConsumerManager consumerManager) {
        this.consumerManager = consumerManager;
    }

    // Response of registering user event listener
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, UserResponseEvent> userKafkaListenerContainerFactory() {
        return consumerManager.getContainerFactory("user", UserResponseEvent.class);
    }
}