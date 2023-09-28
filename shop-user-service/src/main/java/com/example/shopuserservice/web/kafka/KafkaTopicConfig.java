package com.example.shopuserservice.web.kafka;

import com.example.commondto.kafka.KafkaTopic;
import com.example.commondto.kafka.KafkaTopicPartition;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import javax.annotation.PostConstruct;


@Configuration
@ConditionalOnProperty(value = "kafka.enabled", matchIfMissing = true)
public class KafkaTopicConfig {
    @Autowired
    private KafkaAdmin kafkaAdmin;

    @Value("${kafka.broker.number}")
    private int brokerNum;

    /**
     * Create topics with the number of partition and replica.
     */
    @PostConstruct
    public void init() {
        kafkaAdmin.createOrModifyTopics(generateTopic(KafkaTopic.userReq,Integer.parseInt(KafkaTopicPartition.userReq),brokerNum));
        kafkaAdmin.createOrModifyTopics(generateTopic(KafkaTopic.userRes,Integer.parseInt(KafkaTopicPartition.userRes),brokerNum));
        kafkaAdmin.createOrModifyTopics(generateTopic(KafkaTopic.userChatRollback,Integer.parseInt(KafkaTopicPartition.userChatRollback),brokerNum));
        kafkaAdmin.createOrModifyTopics(generateTopic(KafkaTopic.userCustomerRollback,Integer.parseInt(KafkaTopicPartition.userCustomerRollback),brokerNum));
    }

    /**
     * Create topic with the number of partition and replica.
     * The number of partition should be greater than or equal to the number of consumer in a group.
     * `sizeOf.Partition >= sizeOf.Consumer
     * If the number of partition is 2, then 2 consumer can be matched with 2 leader-partition
     *
     * @param topicName
     * @param partitionNum
     * @param brokerNum
     * @return NewTopic
     */
    private NewTopic generateTopic(String topicName,int partitionNum, int brokerNum) {
        return TopicBuilder.name(topicName)
                .partitions(partitionNum) // the number of partitions
                .replicas(brokerNum) // the number of replica sync brokers
                .build(); // topics will have 2 leader-partition, 4 follow-partition
    }

}
