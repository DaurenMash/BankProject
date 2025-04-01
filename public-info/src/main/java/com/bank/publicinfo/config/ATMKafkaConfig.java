package com.bank.publicinfo.config;


import com.bank.publicinfo.dto.ATMDto;
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
public class ATMKafkaConfig {
    private final Map<String, Object> producerConfigs;

    private final Map<String, Object> consumerConfigs;

    @Value("${spring.kafka.trusted-packages}")
    private String trustedPackage;

    @Bean
    public ConsumerFactory<String, ATMDto> atmConsumerFactory() {
        JsonDeserializer<ATMDto> valueDeserializer = new JsonDeserializer(ATMDto.class);
        valueDeserializer.setRemoveTypeHeaders(false);
        valueDeserializer.addTrustedPackages(trustedPackage);
        ErrorHandlingDeserializer<ATMDto> errorHandlingDeserializer = new ErrorHandlingDeserializer((Deserializer)valueDeserializer);
        return (ConsumerFactory<String, ATMDto>)new DefaultKafkaConsumerFactory(this.consumerConfigs, (Deserializer)new StringDeserializer(), (Deserializer)errorHandlingDeserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ATMDto> atmKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ATMDto> factory = new ConcurrentKafkaListenerContainerFactory();
        factory.setConsumerFactory(atmConsumerFactory());
        return factory;
    }

    @Bean
    public ProducerFactory<String, ATMDto> atmDtoProducerFactory() {
        return (ProducerFactory<String, ATMDto>)new DefaultKafkaProducerFactory(producerConfigs);
    }

    @Bean
    public KafkaTemplate<String, ATMDto> atmDtoKafkaTemplate() {
        return new KafkaTemplate(atmDtoProducerFactory());
    }

    @Bean
    public NewTopic atmCreateTopic(@Value("${spring.kafka.topics.atm.create.name}") String topicName, @Value("${spring.kafka.topics.atm.create.partitions}") int partitions, @Value("${spring.kafka.topics.atm.create.replication-factor}") short replicationFactor) {
        return new NewTopic(topicName, partitions, replicationFactor);
    }

    @Bean
    public NewTopic atmUpdateTopic(@Value("${spring.kafka.topics.atm.update.name}") String topicName, @Value("${spring.kafka.topics.atm.update.partitions}") int partitions, @Value("${spring.kafka.topics.atm.update.replication-factor}") short replicationFactor) {
        return new NewTopic(topicName, partitions, replicationFactor);
    }

    @Bean
    public NewTopic atmDeleteTopic(@Value("${spring.kafka.topics.atm.delete.name}") String topicName, @Value("${spring.kafka.topics.atm.delete.partitions}") int partitions, @Value("${spring.kafka.topics.atm.delete.replication-factor}") short replicationFactor) {
        return new NewTopic(topicName, partitions, replicationFactor);
    }

    @Bean
    public NewTopic atmGetTopic(@Value("${spring.kafka.topics.atm.get.name}") String topicName, @Value("${spring.kafka.topics.atm.get.partitions}") int partitions, @Value("${spring.kafka.topics.atm.get.replication-factor}") short replicationFactor) {
        return new NewTopic(topicName, partitions, replicationFactor);
    }

}
