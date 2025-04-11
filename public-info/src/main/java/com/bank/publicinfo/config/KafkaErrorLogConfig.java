package com.bank.publicinfo.config;

import com.bank.publicinfo.dto.ErrorResponseDto;
import java.util.Map;
import lombok.RequiredArgsConstructor;
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
public class KafkaErrorLogConfig {

    private final Map<String, Object> producerConfigs;
    private final Map<String, Object> consumerConfigs;
    @Value("${spring.kafka.trusted-packages}")
    private String trustedPackage;

    @Bean
    public ConsumerFactory<String, ErrorResponseDto> errorLogsConsumerFactory() {
        final JsonDeserializer<ErrorResponseDto> valueDeserializer = new JsonDeserializer<>(ErrorResponseDto.class);
        valueDeserializer.setRemoveTypeHeaders(false);
        valueDeserializer.addTrustedPackages(this.trustedPackage);

        final ErrorHandlingDeserializer<ErrorResponseDto> errorHandlingDeserializer =
                new ErrorHandlingDeserializer<>(valueDeserializer);

        return new DefaultKafkaConsumerFactory<>(
                this.consumerConfigs,
                new StringDeserializer(),
                errorHandlingDeserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ErrorResponseDto> errorLogsKafkaListenerContainerFactory() {
        final ConcurrentKafkaListenerContainerFactory<String, ErrorResponseDto> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(errorLogsConsumerFactory());
        return factory;
    }

    @Bean
    public ProducerFactory<String, ErrorResponseDto> errorLogsProducerFactory() {
        return new DefaultKafkaProducerFactory<>(this.producerConfigs);
    }

    @Bean
    public KafkaTemplate<String, ErrorResponseDto> errorLogsKafkaTemplate() {
        return new KafkaTemplate<>(errorLogsProducerFactory());
    }

}
