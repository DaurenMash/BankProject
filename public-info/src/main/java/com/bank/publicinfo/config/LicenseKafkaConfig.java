package com.bank.publicinfo.config;

import com.bank.publicinfo.dto.LicenseDto;
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
public class LicenseKafkaConfig {
    private final Map<String, Object> producerConfigs;

    private final Map<String, Object> consumerConfigs;

    @Value("${spring.kafka.trusted-packages}")
    private String trustedPackage;

    @Bean
    public ConsumerFactory<String, LicenseDto> licenseConsumerFactory() {
        final JsonDeserializer<LicenseDto> valueDeserializer = new JsonDeserializer<>(LicenseDto.class);
        valueDeserializer.setRemoveTypeHeaders(false);
        valueDeserializer.addTrustedPackages(this.trustedPackage);
        final ErrorHandlingDeserializer<LicenseDto> errorHandlingDeserializer =
                new ErrorHandlingDeserializer<>(valueDeserializer);
        return new DefaultKafkaConsumerFactory<>(
                this.consumerConfigs,
                new StringDeserializer(),
                errorHandlingDeserializer
        );
    }


    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, LicenseDto> licenseKafkaListenerContainerFactory() {
        final ConcurrentKafkaListenerContainerFactory<String, LicenseDto> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(licenseConsumerFactory());
        return factory;
    }

    @Bean
    public ProducerFactory<String, LicenseDto> licenseProducerFactory() {
        return new DefaultKafkaProducerFactory<>(this.producerConfigs);
    }


    @Bean
    public KafkaTemplate<String, LicenseDto> licenseKafkaTemplate() {
        return new KafkaTemplate<>(licenseProducerFactory());
    }


    @Bean
    public NewTopic licenseDtoCreateTopic(
            @Value("${spring.kafka.topics.license.create.name}") String topicName,
            @Value("${spring.kafka.topics.license.create.partitions}") int partitions,
            @Value("${spring.kafka.topics.license.create.replication-factor}") short factor) {
        return new NewTopic(topicName, partitions, factor);
    }

    @Bean
    public NewTopic licenseDtoUpdateTopic(
            @Value("${spring.kafka.topics.license.update.name}") String topicName,
            @Value("${spring.kafka.topics.license.update.partitions}") int partitions,
            @Value("${spring.kafka.topics.license.update.replication-factor}") short factor) {
        return new NewTopic(topicName, partitions, factor);
    }

    @Bean
    public NewTopic licenseDtoDeleteTopic(
            @Value("${spring.kafka.topics.license.delete.name}") String topicName,
            @Value("${spring.kafka.topics.license.delete.partitions}") int partitions,
            @Value("${spring.kafka.topics.license.delete.replication-factor}") short factor) {
        return new NewTopic(topicName, partitions, factor);
    }

    @Bean
    public NewTopic licenseDtoGetTopic(
            @Value("${spring.kafka.topics.license.get.name}") String topicName,
            @Value("${spring.kafka.topics.license.get.partitions}") int partitions,
            @Value("${spring.kafka.topics.license.get.replication-factor}") short replicationFactor) {
        return new NewTopic(topicName, partitions, replicationFactor);
    }
}
