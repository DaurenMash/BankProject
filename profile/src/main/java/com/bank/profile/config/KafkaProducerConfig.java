package com.bank.profile.config;

import com.bank.profile.dto.AccountDetailsDto;
import com.bank.profile.dto.AuditDto;
import com.bank.profile.dto.ErrorDto;
import com.bank.profile.dto.ProfileDto;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    private final Map<String, Object> builtKafkaProperties;

    @SuppressWarnings("removal")
    public KafkaProducerConfig(KafkaProperties kafkaProperties) {
        builtKafkaProperties = kafkaProperties.buildProducerProperties();
    }

    @Bean
    public KafkaTemplate<String, ProfileDto> kafkaTemplateProfile() {
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(builtKafkaProperties));
    }

    @Bean
    public KafkaTemplate<String, AccountDetailsDto> kafkaTemplateAccountDetails() {
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(builtKafkaProperties));
    }

    @Bean
    public KafkaTemplate<String, AuditDto> kafkaTemplateAudit() {
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(builtKafkaProperties));
    }

    @Bean
    public KafkaTemplate<String, ErrorDto> kafkaTemplateError() {
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(builtKafkaProperties));
    }

    @Bean
    public KafkaTemplate<String, Long> kafkaTemplateById() {
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(builtKafkaProperties));
    }
}
