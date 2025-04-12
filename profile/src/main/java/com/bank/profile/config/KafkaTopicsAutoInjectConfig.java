package com.bank.profile.config;

import com.bank.profile.util.KafkaTopic;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

@Profile("kafka-auto-topics")
@Configuration
@RequiredArgsConstructor
public class KafkaTopicsAutoInjectConfig {

    private final KafkaTopic topics;

    @Bean
    public KafkaAdmin.NewTopics topics() {
        return new KafkaAdmin.NewTopics(
                TopicBuilder.name(topics.getTopicProfileCreate()).build(),
                TopicBuilder.name(topics.getTopicProfileUpdate()).build(),
                TopicBuilder.name(topics.getTopicProfileDelete()).build(),
                TopicBuilder.name(topics.getTopicProfileGet()).build(),
                TopicBuilder.name(topics.getTopicProfileGetResponse()).build(),

                TopicBuilder.name(topics.getTopicAccountDetailsCreate()).build(),
                TopicBuilder.name(topics.getTopicAccountDetailsUpdate()).build(),
                TopicBuilder.name(topics.getTopicAccountDetailsDelete()).build(),
                TopicBuilder.name(topics.getTopicAccountDetailsGet()).build(),
                TopicBuilder.name(topics.getTopicAccountDetailsGetResponse()).build(),

                TopicBuilder.name(topics.getTopicAudit()).build(),
                TopicBuilder.name(topics.getTopicError()).build()
        );
    }
}