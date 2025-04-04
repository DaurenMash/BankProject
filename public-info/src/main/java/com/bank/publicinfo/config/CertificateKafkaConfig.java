package com.bank.publicinfo.config;

import com.bank.publicinfo.dto.CertificateDto;
import java.util.Map;
import lombok.Generated;
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
public class CertificateKafkaConfig {
    private final Map<String, Object> producerConfigs;

    private final Map<String, Object> consumerConfigs;

    @Value("${spring.kafka.trusted-packages}")
    private String trustedPackage;

    @Generated
    public CertificateKafkaConfig(Map<String, Object> producerConfigs, Map<String, Object> consumerConfigs) {
        this.producerConfigs = producerConfigs;
        this.consumerConfigs = consumerConfigs;
    }

    @Bean
    public ConsumerFactory<String, CertificateDto> certificateConsumerFactory() {
        JsonDeserializer<CertificateDto> valueDeserializer = new JsonDeserializer(CertificateDto.class);
        valueDeserializer.setRemoveTypeHeaders(false);
        valueDeserializer.addTrustedPackages(new String[] { this.trustedPackage });
        ErrorHandlingDeserializer<CertificateDto> errorHandlingDeserializer = new ErrorHandlingDeserializer((Deserializer)valueDeserializer);
        return (ConsumerFactory<String, CertificateDto>)new DefaultKafkaConsumerFactory(this.consumerConfigs, (Deserializer)new StringDeserializer(), (Deserializer)errorHandlingDeserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, CertificateDto> certificateKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, CertificateDto> factory = new ConcurrentKafkaListenerContainerFactory();
        factory.setConsumerFactory(certificateConsumerFactory());
        return factory;
    }

    @Bean
    public ProducerFactory<String, CertificateDto> certificateProducerFactory() {
        return (ProducerFactory<String, CertificateDto>)new DefaultKafkaProducerFactory(this.producerConfigs);
    }

    @Bean
    public KafkaTemplate<String, CertificateDto> certificateKafkaTemplate() {
        return new KafkaTemplate(certificateProducerFactory());
    }

    @Bean
    public NewTopic certificateCreateTopic(@Value("${spring.kafka.topics.certificate.create.name}") String topicName, @Value("${spring.kafka.topics.certificate.create.partitions}") int partitions, @Value("${spring.kafka.topics.certificate.create.replication-factor}") short replicationFactor) {
        return new NewTopic(topicName, partitions, replicationFactor);
    }

    @Bean
    public NewTopic certificateUpdateTopic(@Value("${spring.kafka.topics.certificate.update.name}") String topicName, @Value("${spring.kafka.topics.certificate.update.partitions}") int partitions, @Value("${spring.kafka.topics.certificate.update.replication-factor}") short replicationFactor) {
        return new NewTopic(topicName, partitions, replicationFactor);
    }

    @Bean
    public NewTopic certificateDeleteTopic(@Value("${spring.kafka.topics.certificate.delete.name}") String topicName, @Value("${spring.kafka.topics.certificate.delete.partitions}") int partitions, @Value("${spring.kafka.topics.certificate.delete.replication-factor}") short replicationFactor) {
        return new NewTopic(topicName, partitions, replicationFactor);
    }

    @Bean
    public NewTopic certificateGetTopic(@Value("${spring.kafka.topics.certificate.get.name}") String topicName, @Value("${spring.kafka.topics.certificate.get.partitions}") int partitions, @Value("${spring.kafka.topics.certificate.get.replication-factor}") short replicationFactor) {
        return new NewTopic(topicName, partitions, replicationFactor);
    }
}
