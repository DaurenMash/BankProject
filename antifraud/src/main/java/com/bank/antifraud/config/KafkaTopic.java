package com.bank.antifraud.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;


@Configuration
public class KafkaTopic {

    @Bean
    public NewTopic createTopic() {
        return TopicBuilder.name("suspicious-transfers.create").partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic updateTopic() {
        return TopicBuilder.name("suspicious-transfers.update").partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic deleteTopic() {
        return TopicBuilder.name("suspicious-transfers.delete").partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic getTopic() {
        return TopicBuilder.name("suspicious-transfers.get").partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic responseTopic() {
        return TopicBuilder.name("suspicious-transfers.Response").partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic logsTopic() {
        return TopicBuilder.name("audit.logs").partitions(3).replicas(1).build();
    }

}

