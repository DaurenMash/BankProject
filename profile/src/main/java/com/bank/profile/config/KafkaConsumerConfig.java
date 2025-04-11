package com.bank.profile.config;

import com.bank.profile.dto.AccountDetailsDto;
import com.bank.profile.dto.ProfileDto;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    private final Map<String, Object> builtKafkaProperties;

    @SuppressWarnings("removal")
    public KafkaConsumerConfig(KafkaProperties kafkaProperties) {
        builtKafkaProperties = kafkaProperties.buildConsumerProperties();
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ProfileDto> listenerFactoryProfile() {
        ConcurrentKafkaListenerContainerFactory<String, ProfileDto> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(builtKafkaProperties));
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, AccountDetailsDto> listenerFactoryAccountDetails() {
        ConcurrentKafkaListenerContainerFactory<String, AccountDetailsDto> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(builtKafkaProperties));
        return factory;
    }
}
