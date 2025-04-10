package com.bank.publicinfo.config;

import com.bank.publicinfo.dto.ATMDto;
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
public class KafkaATMConfig {

    private final Map<String, Object> producerConfigs;
    private final Map<String, Object> consumerConfigs;

    @Value("${spring.kafka.trusted-packages}")
    private String trustedPackage;

    @Bean
    public ConsumerFactory<String, ATMDto> atmConsumerFactory() {
        final JsonDeserializer<ATMDto> valueDeserializer = new JsonDeserializer<>(ATMDto.class);
        valueDeserializer.setRemoveTypeHeaders(false);
        valueDeserializer.addTrustedPackages(trustedPackage);
        final ErrorHandlingDeserializer<ATMDto> errorHandlingDeserializer =
                new ErrorHandlingDeserializer<>(valueDeserializer);
        return new DefaultKafkaConsumerFactory<>(this.consumerConfigs,
                new StringDeserializer(), errorHandlingDeserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ATMDto> atmKafkaListenerContainerFactory() {
        final ConcurrentKafkaListenerContainerFactory<String, ATMDto> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(atmConsumerFactory());
        return factory;
    }

    @Bean
    public ProducerFactory<String, ATMDto> atmDtoProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs);
    }

    @Bean
    public KafkaTemplate<String, ATMDto> atmDtoKafkaTemplate() {
        return new KafkaTemplate<>(atmDtoProducerFactory());
    }

}
