package com.example.orderservice.kafka;

import com.example.commondto.kafka.KafkaTopic;
import com.example.commondto.kafka.KafkaTopicPartition;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import javax.annotation.PostConstruct;


@Configuration
public class KafkaTopicConfig {
    @Autowired
    private KafkaAdmin kafkaAdmin;
    
    private NewTopic generateTopic(String topicName,int partitionNum, int brokerNum) {
        return TopicBuilder.name(topicName)
                .partitions(partitionNum) // 할당하고자 하는 파티션 개수
                .replicas(brokerNum) // replica sync를 위한 broker 개수
                .build(); // 토픽은 총 2개의 leader-partition, 4개의 follow-partition 로 설정할 것임
    }

    @PostConstruct
    public void init() {
        kafkaAdmin.createOrModifyTopics(generateTopic(KafkaTopic.orderNewOrderReq, Integer.parseInt(KafkaTopicPartition.orderNewOrderReq),3));
        kafkaAdmin.createOrModifyTopics(generateTopic(KafkaTopic.customerNewOrderRes, Integer.parseInt(KafkaTopicPartition.customerNewOrderRes),3));
        kafkaAdmin.createOrModifyTopics(generateTopic(KafkaTopic.productNewOrderRes, Integer.parseInt(KafkaTopicPartition.productNewOrderRes),3));
    }
}
