package com.bank.profile.config;

import com.bank.profile.dto.AccountDetailsDto;
import com.bank.profile.dto.ProfileDto;
import com.bank.profile.util.KafkaTopic;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;

@Configuration
@Slf4j
public class KafkaErrorHandlerConfig {
    @Bean
    public DefaultErrorHandler errorHandlerProfile(KafkaTemplate<String, ProfileDto> kafkaTemplate, KafkaTopic topics) {
        return new DefaultErrorHandler(
                new DeadLetterPublishingRecoverer(kafkaTemplate, (record, ex) -> {
                    log.error("Sending to DLT: {} due to error: {}", record.value(), ex.getMessage());
                    return new TopicPartition(topics.getTopicError(), record.partition());
                })
        );
    }

    @Bean
    public DefaultErrorHandler errorHandlerAccount(KafkaTemplate<String, AccountDetailsDto> kafkaTemplate, KafkaTopic topics) {
        return new DefaultErrorHandler(
                new DeadLetterPublishingRecoverer(kafkaTemplate, (record, ex) -> {
                    log.error("Sending to DLT: {} due to error: {}", record.value(), ex.getMessage());
                    return new TopicPartition(topics.getTopicError(), record.partition());
                })
        );
    }
}