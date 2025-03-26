package com.bank.antifraud.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;


@Configuration
public class KafkaTopic {

    @Bean
    public NewTopic CreateTopic() {
        return TopicBuilder.name("suspicious-transfers.create").build();
    }

    @Bean
    public NewTopic UpdateTopic() {
        return TopicBuilder.name("suspicious-transfers.update").build();
    }

    @Bean
    public NewTopic DeleteTopic() {
        return TopicBuilder.name("suspicious-transfers.delete").build();
    }

    @Bean
    public NewTopic GetTopic() {
        return TopicBuilder.name("suspicious-transfers.get").build();
    }

    @Bean
    public NewTopic ResponseTopic() {
        return TopicBuilder.name("suspicious-transfers.Response").build();
    }

    @Bean
    public NewTopic LogsTopic() {
        return TopicBuilder.name("audit.logs").build();
    }

}

