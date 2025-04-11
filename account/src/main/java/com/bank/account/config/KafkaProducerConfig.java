package com.bank.account.config;

import com.bank.account.dto.AccountDto;
import com.bank.account.dto.AuditDto;
import com.bank.account.dto.KafkaRequest;
import com.bank.account.exception.error_dto.ErrorResponse;
import lombok.Generated;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@EnableKafka
@Generated
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public ProducerFactory<String, AccountDto> accountProducerFactory() {
        final Map<String, Object> configProps = setProducerProps();
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public ProducerFactory<String, AuditDto> auditProducerFactory() {
        final Map<String, Object> configProps = setProducerProps();
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public ProducerFactory<String, String> stringProducerFactory() {
        final Map<String, Object> configProps = setProducerProps();
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public ProducerFactory<String, KafkaRequest> kafkaRequestProducerFactory() {
        final Map<String, Object> configProps = setProducerProps();
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public ProducerFactory<String, List<AccountDto>> accountsListProducerFactory() {
        final Map<String, Object> configProps = setProducerProps();
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public ProducerFactory<String, ErrorResponse> errorResponseProducerFactory() {
        final Map<String, Object> configProps = setProducerProps();
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, String> stringKafkaTemplate() {
        return new KafkaTemplate<>(stringProducerFactory());
    }

    @Bean
    public KafkaTemplate<String, KafkaRequest> kafkaRequestKafkaTemplate() {
        return new KafkaTemplate<>(kafkaRequestProducerFactory());
    }

    @Bean
    public KafkaTemplate<String, List<AccountDto>> accountsListKafkaTemplate() {
        return new KafkaTemplate<>(accountsListProducerFactory());
    }

    @Bean
    public KafkaTemplate<String, AccountDto> accountKafkaTemplate() {
        return new KafkaTemplate<>(accountProducerFactory());
    }

    @Bean
    public KafkaTemplate<String, ErrorResponse> errorResponseKafkaTemplate() {
        return new KafkaTemplate<>(errorResponseProducerFactory());
    }

    @Bean
    public KafkaTemplate<String, AuditDto> auditKafkaTemplate() {
        return new KafkaTemplate<>(auditProducerFactory());
    }

    private Map<String, Object> setProducerProps() {
        final Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return configProps;
    }
}
