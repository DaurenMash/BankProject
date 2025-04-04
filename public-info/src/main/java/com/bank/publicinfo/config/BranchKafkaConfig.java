package com.bank.publicinfo.config;


import com.bank.publicinfo.dto.BranchDto;
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
public class BranchKafkaConfig {
    private final Map<String, Object> producerConfigs;

    private final Map<String, Object> consumerConfigs;

    @Value("${spring.kafka.trusted-packages}")
    private String trustedPackage;



    @Bean
    public ConsumerFactory<String, BranchDto> branchConsumerFactory() {
        JsonDeserializer<BranchDto> valueDeserializer = new JsonDeserializer(BranchDto.class);
        valueDeserializer.setRemoveTypeHeaders(false);
        valueDeserializer.addTrustedPackages(new String[] { this.trustedPackage });
        ErrorHandlingDeserializer<BranchDto> errorHandlingDeserializer = new ErrorHandlingDeserializer((Deserializer)valueDeserializer);
        return (ConsumerFactory<String, BranchDto>)new DefaultKafkaConsumerFactory(this.consumerConfigs, (Deserializer)new StringDeserializer(), (Deserializer)errorHandlingDeserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, BranchDto> branchKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, BranchDto> factory = new ConcurrentKafkaListenerContainerFactory();
        factory.setConsumerFactory(branchConsumerFactory());
        return factory;
    }

    @Bean
    public ProducerFactory<String, BranchDto> branchDtoProducerFactory() {
        return (ProducerFactory<String, BranchDto>)new DefaultKafkaProducerFactory(this.producerConfigs);
    }

    @Bean
    public KafkaTemplate<String, BranchDto> branchDtoKafkaTemplate() {
        return new KafkaTemplate(branchDtoProducerFactory());
    }

    @Bean
    public NewTopic branchCreateTopic(@Value("${spring.kafka.topics.branch.create.name}") String topicName, @Value("${spring.kafka.topics.branch.create.partitions}") int partitions, @Value("${spring.kafka.topics.branch.create.replication-factor}") short replicationFactor) {
        return new NewTopic(topicName, partitions, replicationFactor);
    }

    @Bean
    public NewTopic branchUpdateTopic(@Value("${spring.kafka.topics.branch.update.name}") String topicName, @Value("${spring.kafka.topics.branch.update.partitions}") int partitions, @Value("${spring.kafka.topics.branch.update.replication-factor}") short replicationFactor) {
        return new NewTopic(topicName, partitions, replicationFactor);
    }

    @Bean
    public NewTopic branchDeleteTopic(@Value("${spring.kafka.topics.branch.delete.name}") String topicName, @Value("${spring.kafka.topics.branch.delete.partitions}") int partitions, @Value("${spring.kafka.topics.branch.delete.replication-factor}") short replicationFactor) {
        return new NewTopic(topicName, partitions, replicationFactor);
    }

    @Bean
    public NewTopic branchGetTopic(@Value("${spring.kafka.topics.branch.get.name}") String topicName, @Value("${spring.kafka.topics.branch.get.partitions}") int partitions, @Value("${spring.kafka.topics.branch.get.replication-factor}") short replicationFactor) {
        return new NewTopic(topicName, partitions, replicationFactor);
    }
}

