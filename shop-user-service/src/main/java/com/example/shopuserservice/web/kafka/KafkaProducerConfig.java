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
import java.util.UUID;

@Configuration
@EnableKafka
public class KafkaProducerConfig {

    @Value("${kafka.bootstrap}")
    private String bootstrapServer;
    private final String batchSize = Integer.toString(32*1024);
    private final Integer linger = 50; // ms

    private ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        // 메세지 재전송은 10번만!
        configProps.put(ProducerConfig.RETRIES_CONFIG,10);

        // 카프카 트랜젝션에 id 부여
        // 사실 우리는 non-transactional 이라서 딱히 필요는 없습니다.
        configProps.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, UUID.randomUUID().toString());

        // 카프카 메세지 배치 프로세스
        configProps.put(ProducerConfig.BATCH_SIZE_CONFIG, batchSize);
        configProps.put(ProducerConfig.LINGER_MS_CONFIG, linger);

        // 메세지 전송 실패 시 재전송을 하게 되는데, 이 때 재발행한 메세지를 구분하기 위해 별도로 구분자를 추가한다.
        configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");

        DefaultKafkaProducerFactory<String, Object> pf = new DefaultKafkaProducerFactory<>(configProps);
        pf.setTransactionIdPrefix("user");

        // Kafka/Topic/Partition 별 스레드 설정
        pf.setProducerPerThread(true);

        return pf;
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaProducerTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

}