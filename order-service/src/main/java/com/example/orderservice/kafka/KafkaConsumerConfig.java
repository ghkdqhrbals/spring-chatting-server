package com.example.orderservice.kafka;


import com.example.commondto.events.order.OrderEvent;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

@Slf4j
@EnableKafka
@Configuration
public class KafkaConsumerConfig {
    @Value("${kafka.bootstrap-server}")
    private String bootstrapServer;

    // 주문 이벤트 수신
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OrderEvent> userKafkaListenerContainerFactory() {
        return getContainerFactory("order",OrderEvent.class);
    }

    /**
     * 유틸 목록
     * --Methods--
     * getContainerFactory()
     * getKafkaConsumerFactory()
     * setConfig()
     * setDeserializer()
     */

    private <T> ConcurrentKafkaListenerContainerFactory<String, T> getContainerFactory(String groupId, Class<T> classType) {
        ConcurrentKafkaListenerContainerFactory<String, T> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(getKafkaConsumerFactory(groupId, classType));
        return factory;
    }

    private <T> DefaultKafkaConsumerFactory<String, T> getKafkaConsumerFactory(String groupId,Class<T> classType) {
        JsonDeserializer<T> deserializer = setDeserializer(classType);
        return new DefaultKafkaConsumerFactory<>(setConfig(groupId, deserializer), new StringDeserializer(), deserializer);
    }

    private <T> ImmutableMap<String, Object> setConfig(String groupId, JsonDeserializer<T> deserializer) {
        ImmutableMap<String, Object> config = ImmutableMap.<String, Object>builder()
                .put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer)
                .put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class)
                .put(ConsumerConfig.GROUP_ID_CONFIG, groupId)
                .put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializer)
                .build();
        return config;
    }

    private <T> JsonDeserializer<T> setDeserializer(Class<T> classType) {
        JsonDeserializer<T> deserializer = new JsonDeserializer<>(classType);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(true);
        return deserializer;
    }
}