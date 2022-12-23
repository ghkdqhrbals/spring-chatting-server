package chatting.chat.web.kafka;

import chatting.chat.web.kafka.dto.*;
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
    @Value("${kafka.bootstrap-servers}")
    private String bootstrapServer;

    // 로그인 요청
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, RequestLoginDTO> loginKafkaListenerContainerFactory() {
        return getContainerFactory("defaultGroup",RequestLoginDTO.class);
    }
    // 로그아웃
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> logoutKafkaListenerContainerFactory() {
        return getContainerFactory("defaultGroup",String.class);
    }
    // 유저 참여 채팅방 목록 조회
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, RequestUserRoomDTO> userRoomKafkaListenerContainerFactory() {
        return getContainerFactory("defaultGroup",RequestUserRoomDTO.class);
    }
    // 유저 친구목록 조회
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, RequestUserFriendDTO> userSearchFriendKafkaListenerContainerFactory() {
        return getContainerFactory("defaultGroup",RequestUserFriendDTO.class);
    }
    // 유저 저장
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, RequestAddUserDTO> userAddKafkaListenerContainerFactory() {
        return getContainerFactory("defaultGroup",RequestAddUserDTO.class);
    }
    // 유저 상태메세지 변경
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, RequestChangeUserStatusDTO> userChangeStatusKafkaListenerContainerFactory() {
        return getContainerFactory("defaultGroup",RequestChangeUserStatusDTO.class);
    }
    // 채팅방 개설
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, RequestAddUserRoomDTO> userAddRoomKafkaListenerContainerFactory() {
        return getContainerFactory("defaultGroup",RequestAddUserRoomDTO.class);
    }
    // 채팅 저장
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, RequestAddChatMessageDTO> userAddChatKafkaListenerContainerFactory() {
        return getContainerFactory("defaultGroup",RequestAddChatMessageDTO.class);
    }
    // 유저 친구 저장
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, RequestAddFriendDTO> userAddFriendKafkaListenerContainerFactory() {
        return getContainerFactory("defaultGroup",RequestAddFriendDTO.class);
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