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
public class KafkaConfig {

    private final Map<String, Object> builtKafkaProperties;

    @SuppressWarnings("removal")
    public KafkaConfig(KafkaProperties kafkaProperties) {
        builtKafkaProperties = kafkaProperties.buildProducerProperties();
    }

    @Bean
    public KafkaTemplate<String, ProfileDto> kafkaTemplateProfile() {
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(builtKafkaProperties));
    }
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ProfileDto> listenerFactoryProfile() {
        ConcurrentKafkaListenerContainerFactory<String, ProfileDto> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(builtKafkaProperties));
        return factory;
    }

    @Bean
    public KafkaTemplate<String, AccountDetailsDto> kafkaTemplateAccountDetails() {
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(builtKafkaProperties));
    }
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, AccountDetailsDto> listenerFactoryAccountDetails() {
        ConcurrentKafkaListenerContainerFactory<String, AccountDetailsDto> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(builtKafkaProperties));
        return factory;
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
