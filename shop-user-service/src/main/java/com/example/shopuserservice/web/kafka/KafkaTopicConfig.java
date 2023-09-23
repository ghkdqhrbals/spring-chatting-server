package com.example.shopuserservice.web.kafka;

import com.example.commondto.kafka.KafkaTopic;
import com.example.commondto.kafka.KafkaTopicPartition;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import javax.annotation.PostConstruct;


@Configuration
@ConditionalOnProperty(value = "kafka.enabled", matchIfMissing = true)
public class KafkaTopicConfig {
    @Autowired
    private KafkaAdmin kafkaAdmin;

    /**
     * Create topics with the number of partition and replica.
     */
    @PostConstruct
    public void init() {
        kafkaAdmin.createOrModifyTopics(generateTopic(KafkaTopic.userReq,Integer.parseInt(KafkaTopicPartition.userReq),3));
        kafkaAdmin.createOrModifyTopics(generateTopic(KafkaTopic.userRes,Integer.parseInt(KafkaTopicPartition.userRes),3));
        kafkaAdmin.createOrModifyTopics(generateTopic(KafkaTopic.userChatRollback,Integer.parseInt(KafkaTopicPartition.userChatRollback),3));
        kafkaAdmin.createOrModifyTopics(generateTopic(KafkaTopic.userCustomerRollback,Integer.parseInt(KafkaTopicPartition.userCustomerRollback),3));
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
