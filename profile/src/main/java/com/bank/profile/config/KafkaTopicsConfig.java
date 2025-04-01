package com.bank.profile.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

@Configuration
@Getter
public class KafkaTopicsConfig {

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @Value("${app.kafka.topic.profile.create}")
    private String topicProfileCreate;
    @Value("${app.kafka.topic.profile.update}")
    private String topicProfileUpdate;
    @Value("${app.kafka.topic.profile.delete}")
    private String topicProfileDelete;
    @Value("${app.kafka.topic.profile.get}")
    private String topicProfileGet;
    @Value("${app.kafka.topic.profile.get-response}")
    private String topicProfileGetResponse;
    @Value("${app.kafka.topic.account.create}")
    private String topicAccountDetailsCreate;
    @Value("${app.kafka.topic.account.update}")
    private String topicAccountDetailsUpdate;
    @Value("${app.kafka.topic.account.delete}")
    private String topicAccountDetailsDelete;
    @Value("${app.kafka.topic.account.get}")
    private String topicAccountDetailsGet;
    @Value("${app.kafka.topic.account.get-response}")
    private String topicAccountDetailsGetResponse;
    @Value("${app.kafka.topic.audit}")
    private String topicAudit;
    @Value("${app.kafka.topic.error}")
    private String topicError;

    @Profile("kafka-auto-topics")
    @Bean
    public KafkaAdmin.NewTopics topics() {
        return new KafkaAdmin.NewTopics(
                TopicBuilder.name(topicProfileCreate).build(),
                TopicBuilder.name(topicProfileUpdate).build(),
                TopicBuilder.name(topicProfileDelete).build(),
                TopicBuilder.name(topicProfileGet).build(),
                TopicBuilder.name(topicProfileGetResponse).build(),

                TopicBuilder.name(topicAccountDetailsCreate).build(),
                TopicBuilder.name(topicAccountDetailsUpdate).build(),
                TopicBuilder.name(topicAccountDetailsDelete).build(),
                TopicBuilder.name(topicAccountDetailsGet).build(),
                TopicBuilder.name(topicAccountDetailsGetResponse).build(),

                TopicBuilder.name(topicAudit).build(),
                TopicBuilder.name(topicError).build()
        );
    }
}
