package chatting.chat.web.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import javax.annotation.PostConstruct;


@Configuration
public class KafkaTopicConfig extends KafkaTopicConst {
    @Autowired
    private KafkaAdmin kafkaAdmin;

    // 계산 잘 해야한다. Partition 개수 >= Group내 Conusmer 개수
    // 생성하고자 하는 Conumser=2 Partition은 2이기에, 각각 conusmer에게 leader-partition 매칭가능
    private NewTopic generateTopic(String topicName,int partitionNum, int brokerNum) {
        return TopicBuilder.name(topicName)
                .partitions(partitionNum) // 할당하고자 하는 파티션 개수
                .replicas(brokerNum) // replica sync를 위한 broker 개수
                .build(); // 토픽은 총 2개의 leader-partition, 4개의 follow-partition 로 설정할 것임
    }

    @PostConstruct
    public void init() {
        kafkaAdmin.createOrModifyTopics(generateTopic(TOPIC_USER_CHANGE,2,3));
    }
}
