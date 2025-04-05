package com.bank.publicinfo.config;


import com.bank.publicinfo.dto.BankDetailsDto;

import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
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
public class BankDetailsKafkaConfig {
    private final Map<String, Object> producerConfigs;

    private final Map<String, Object> consumerConfigs;

    @Value("${spring.kafka.trusted-packages}")
    private String trustedPackage;


    @Bean
    public ConsumerFactory<String, BankDetailsDto> consumerFactory() {
        final JsonDeserializer<BankDetailsDto> valueDeserializer = new JsonDeserializer<>(BankDetailsDto.class);
        valueDeserializer.setRemoveTypeHeaders(false);
        valueDeserializer.addTrustedPackages(this.trustedPackage);
        final ErrorHandlingDeserializer<BankDetailsDto> errorHandlingDeserializer =
                new ErrorHandlingDeserializer<>(valueDeserializer);
        return new DefaultKafkaConsumerFactory<>(
                this.consumerConfigs,
                new StringDeserializer(),
                errorHandlingDeserializer
        );
    }


    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, BankDetailsDto> kafkaListenerContainerFactory() {
        final ConcurrentKafkaListenerContainerFactory<String, BankDetailsDto> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

    @Bean
    public ProducerFactory<String, BankDetailsDto> bankDetailsDtoProducerFactory() {
        return new DefaultKafkaProducerFactory<>(this.producerConfigs);
    }


    @Bean
    public KafkaTemplate<String, BankDetailsDto> bankDetailsDtoKafkaTemplate() {
        return new KafkaTemplate<>(bankDetailsDtoProducerFactory());
    }

    @Bean
    public NewTopic bankCreateTopic(@Value("${spring.kafka.topics.bank.create.name}") String topicName,
                                    @Value("${spring.kafka.topics.bank.create.partitions}") int partitions,
                                    @Value("${spring.kafka.topics.bank.create.replication-factor}") short factor) {
        return new NewTopic(topicName, partitions, factor);
    }

    @Bean
    public NewTopic bankUpdateTopic(@Value("${spring.kafka.topics.bank.update.name}") String topicName,
                                    @Value("${spring.kafka.topics.bank.update.partitions}") int partitions,
                                    @Value("${spring.kafka.topics.bank.update.replication-factor}") short factor) {
        return new NewTopic(topicName, partitions, factor);
    }

    @Bean
    public NewTopic bankDeleteTopic(@Value("${spring.kafka.topics.bank.delete.name}") String topicName,
                                    @Value("${spring.kafka.topics.bank.delete.partitions}") int partitions,
                                    @Value("${spring.kafka.topics.bank.delete.replication-factor}") short factor) {
        return new NewTopic(topicName, partitions, factor);
    }

    @Bean
    public NewTopic bankGetTopic(@Value("${spring.kafka.topics.bank.get.name}") String topicName,
                                 @Value("${spring.kafka.topics.bank.get.partitions}") int partitions,
                                 @Value("${spring.kafka.topics.bank.get.replication-factor}") short factor) {
        return new NewTopic(topicName, partitions, factor);
    }
}
