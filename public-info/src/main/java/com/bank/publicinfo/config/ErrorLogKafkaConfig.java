package com.bank.publicinfo.config;

import com.bank.publicinfo.dto.ErrorResponseDto;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

@Configuration
@EnableKafka
@RequiredArgsConstructor
public class ErrorLogKafkaConfig {

    private final Map<String, Object> producerConfigs;
    private final Map<String, Object> consumerConfigs;

    @Value("${spring.kafka.trusted-packages}")
    private String trustedPackage;

    @Bean
    public ConsumerFactory<String, ErrorResponseDto> errorLogsConsumerFactory() {
        JsonDeserializer<ErrorResponseDto> valueDeserializer = new JsonDeserializer<>(ErrorResponseDto.class);
        valueDeserializer.setRemoveTypeHeaders(false);
        valueDeserializer.addTrustedPackages(this.trustedPackage);
        ErrorHandlingDeserializer<ErrorResponseDto> errorHandlingDeserializer = new ErrorHandlingDeserializer<>(valueDeserializer);
        return new DefaultKafkaConsumerFactory<>(consumerConfigs, new StringDeserializer(), errorHandlingDeserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ErrorResponseDto> errorLogsKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ErrorResponseDto> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(errorLogsConsumerFactory());
        return factory;
    }

    @Bean
    public ProducerFactory<String, ErrorResponseDto> errorLogsProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs);
    }

    @Bean
    public KafkaTemplate<String, ErrorResponseDto> errorLogsKafkaTemplate() {
        return new KafkaTemplate<>(errorLogsProducerFactory());
    }

    @Bean
    public NewTopic errorLogsTopic(
            @Value("${spring.kafka.topics.error-log.name}") String topicName,
            @Value("${spring.kafka.topics.error-log.partitions}") int partitions,
            @Value("${spring.kafka.topics.error-log.replication-factor}") short replicationFactor) {
        return new NewTopic(topicName, partitions, replicationFactor);
    }
}
