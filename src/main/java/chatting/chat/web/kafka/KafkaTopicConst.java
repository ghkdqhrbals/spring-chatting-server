package chatting.chat.web.kafka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class KafkaTopicConst {
    @Value("${kafka.topic-login-request}")
    public String TOPIC_LOGIN_REQUEST;
    @Value("${kafka.topic-login-response}")
    public String TOPIC_LOGIN_RESPONSE;
    @Value("${kafka.topic-logout-request}")
    public String TOPIC_LOGOUT_REQUEST;
    @Value("${kafka.topic-logout-response}")
    public String TOPIC_LOGOUT_RESPONSE;
    @Value("${kafka.topic-user-search-room-request}")
    public String TOPIC_USER_ROOM_REQUEST;
    @Value("${kafka.topic-user-search-room-response}")
    public String TOPIC_USER_ROOM_RESPONSE;
    @Value("${kafka.topic-user-search-room-request}")
    public String TOPIC_USER_SEARCH_ROOM_REQUEST;
    @Value("${kafka.topic-user-search-room-response}")
    public String TOPIC_USER_SEARCH_ROOM_RESPONSE;

    @Value("${kafka.topic-user-search-friend-request}")
    public String TOPIC_USER_SEARCH_FRIEND_REQUEST;
    @Value("${kafka.topic-user-search-friend-response}")
    public String TOPIC_USER_SEARCH_FRIEND_RESPONSE;

    @Value("${kafka.topic-user-add-request}")
    public String TOPIC_USER_ADD_REQUEST;
    @Value("${kafka.topic-user-add-response}")
    public String TOPIC_USER_ADD_RESPONSE;

    @Value("${kafka.topic-user-status-change-request}")
    public String TOPIC_USER_STATUS_CHANGE_REQUEST;
    @Value("${kafka.topic-user-status-change-response}")
    public String TOPIC_USER_STATUS_CHANGE_RESPONSE;

    @Value("${kafka.topic-user-add-room-request}")
    public String TOPIC_USER_ADD_ROOM_REQUEST;
    @Value("${kafka.topic-user-add-room-response}")
    public String TOPIC_USER_ADD_ROOM_RESPONSE;
    @Value("${kafka.topic-user-add-chat-request}")
    public String TOPIC_USER_ADD_CHAT_REQUEST;
    @Value("${kafka.topic-user-add-chat-response}")
    public String TOPIC_USER_ADD_CHAT_RESPONSE;

    @Value("${kafka.topic-user-add-friend-request}")
    public String TOPIC_USER_ADD_FRIEND_REQUEST;
    @Value("${kafka.topic-user-add-friend-response}")
    public String TOPIC_USER_ADD_FRIEND_RESPONSE;


}
