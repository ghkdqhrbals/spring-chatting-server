package com.example.orderservice.kafka;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServer;
    private final String batchSize = Integer.toString(32*1024); // 32KB
    private final Integer linger = 50; // 50ms

    private ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        // Idempotence setting that let the retried messages will have same message id
        configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        // By idempotence setting, retried messages will have same message id
        configProps.put(ProducerConfig.RETRIES_CONFIG,5);
        // Producer cannot resend messages to another leader if the message is having same message id
        configProps.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 1);
        // Message batch process using batchSize and linger.ms
        configProps.put(ProducerConfig.BATCH_SIZE_CONFIG, batchSize); // 32KB
        configProps.put(ProducerConfig.LINGER_MS_CONFIG, linger); // 50ms
        DefaultKafkaProducerFactory<String, Object> pf = new DefaultKafkaProducerFactory<>(configProps);
        return new DefaultKafkaProducerFactory<>(configProps);
    }
    @Bean
    public KafkaTemplate<String, Object> kafkaProducerTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

}