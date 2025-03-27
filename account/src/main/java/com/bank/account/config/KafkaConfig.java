package com.bank.account.config;

import com.bank.account.dto.AccountDto;
import com.bank.account.dto.AuditDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Generated;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
@Generated
@RequiredArgsConstructor
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("{spring.kafka.consumer.auto-offset-reset}")
    private String autoOffsetReset;

    @Value("{spring.kafka.consumer.group-ids.account")
    private String accountGroupId;

    @Value("{spring.kafka.consumer.group-ids.audit")
    private String auditGroupId;

    private final ObjectMapper objectMapper;

    @Bean
    public ProducerFactory<String, AccountDto> accountProducerFactory() {
        final Map<String, Object> configProps = setProducerProps();
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public ProducerFactory<String, AuditDto> auditProducerFactory() {
        Map<String, Object> configProps = setProducerProps();
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, AccountDto> accountKafkaTemplate() {
        return new KafkaTemplate<>(accountProducerFactory());
    }

    @Bean
    public KafkaTemplate<String, AuditDto> auditKafkaTemplate() {
        return new KafkaTemplate<>(auditProducerFactory());
    }

    @Bean
    public ConsumerFactory<String, AccountDto> accountConsumerFactory() {
        Map<String, Object> props = setConsumerProps(accountGroupId);
        JsonDeserializer<AccountDto> jsonDeserializer = new JsonDeserializer<>(AccountDto.class, objectMapper);
        return new DefaultKafkaConsumerFactory<>(props,
                new StringDeserializer(),
                jsonDeserializer);
    }

    @Bean
    public ConsumerFactory<String, AuditDto> auditConsumerFactory() {
        Map<String, Object> props = setConsumerProps(auditGroupId);
        JsonDeserializer<AuditDto> jsonDeserializer = new JsonDeserializer<>(AuditDto.class, objectMapper);
        return new DefaultKafkaConsumerFactory<>(props,
                new StringDeserializer(),
                jsonDeserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, AccountDto> accountKafkaListenerContainerFactory() {
        final ConcurrentKafkaListenerContainerFactory<String, AccountDto> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(accountConsumerFactory());
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, AuditDto> auditKafkaListenerContainerFactory() {
        final ConcurrentKafkaListenerContainerFactory<String, AuditDto> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(auditConsumerFactory());
        return factory;
    }

    private Map<String, Object> setConsumerProps(String groupId) {
        final Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.bank.account.dto");
        return props;
    }

    private Map<String, Object> setProducerProps() {
        final Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return configProps;
    }
}
