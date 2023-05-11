package com.example.shopuserservice.web.kafka;

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

    @Value("${kafka.bootstrap}")
    private String bootstrapServer;
    private final Integer batchSize = 50; // number
    private final Integer linger = 50; // ms

    private ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);


//        configProps.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, 100);

//        configProps.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, 10000);

        configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "false");
        configProps.put(ProducerConfig.RETRIES_CONFIG,0);
        configProps.put(ProducerConfig.BATCH_SIZE_CONFIG, Integer.toString(32*1024));
        configProps.put(ProducerConfig.LINGER_MS_CONFIG, linger);

        DefaultKafkaProducerFactory<String, Object> pf = new DefaultKafkaProducerFactory<>(configProps);
        pf.setProducerPerThread(true);

        return pf;
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaProducerTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

}