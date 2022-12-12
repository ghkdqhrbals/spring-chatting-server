package chatting.chat.web.kafka;

import chatting.chat.web.dto.ChatMessageDTO;
import chatting.chat.web.dto.ChattingDTO;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

@Slf4j
@EnableKafka
@Configuration
public class KafkaConsumerConfig {

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ChatMessageDTO> kafkaListenerContainerFactory() {
        log.info("kafkaListenerContainerFactory");
        ConcurrentKafkaListenerContainerFactory<String, ChatMessageDTO> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, ChatMessageDTO> consumerFactory() {
        log.info("consumerFactory");
        JsonDeserializer<ChatMessageDTO> deserializer = new JsonDeserializer<>(ChatMessageDTO.class);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(true);

        ImmutableMap<String, Object> config = ImmutableMap.<String, Object>builder()
                .put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaConsts.KAFKA_BROKER)
                .put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class)
                .put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializer)
                .put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest")
                .put("group.id", KafkaConsts.GROUP_ID)
                .build();

        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(), deserializer);
    }

}