package chatting.chat.web.kafka;

import com.example.commondto.kafka.KafkaTopic;
import com.example.commondto.kafka.KafkaTopicPartition;
import jakarta.annotation.PostConstruct;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;


@Configuration
@Profile("prod")
public class KafkaTopicConfig {
    private KafkaAdmin kafkaAdmin;

    @Autowired
    public KafkaTopicConfig(KafkaAdmin kafkaAdmin) {
        this.kafkaAdmin = kafkaAdmin;
    }

    @Value("${kafka.broker.number}")
    private int brokerNum;

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
        kafkaAdmin.createOrModifyTopics(generateTopic(KafkaTopic.userReq,Integer.parseInt(KafkaTopicPartition.userReq),brokerNum));
        kafkaAdmin.createOrModifyTopics(generateTopic(KafkaTopic.userRes,Integer.parseInt(KafkaTopicPartition.userRes),brokerNum));
    }
}
