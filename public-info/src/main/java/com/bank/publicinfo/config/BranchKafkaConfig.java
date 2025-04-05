package com.bank.publicinfo.config;


import com.bank.publicinfo.dto.BranchDto;
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
public class BranchKafkaConfig {
    private final Map<String, Object> producerConfigs;

    private final Map<String, Object> consumerConfigs;

    @Value("${spring.kafka.trusted-packages}")
    private String trustedPackage;


    @Bean
    public ConsumerFactory<String, BranchDto> branchConsumerFactory() {
        final JsonDeserializer<BranchDto> valueDeserializer = new JsonDeserializer<>(BranchDto.class);
        valueDeserializer.setRemoveTypeHeaders(false);
        valueDeserializer.addTrustedPackages(this.trustedPackage);

        final ErrorHandlingDeserializer<BranchDto> errorHandlingDeserializer =
                new ErrorHandlingDeserializer<>(valueDeserializer);

        return new DefaultKafkaConsumerFactory<>(
                this.consumerConfigs,
                new StringDeserializer(),
                errorHandlingDeserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, BranchDto> branchKafkaListenerContainerFactory() {
        final ConcurrentKafkaListenerContainerFactory<String, BranchDto> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(branchConsumerFactory());
        return factory;
    }

    @Bean
    public ProducerFactory<String, BranchDto> branchDtoProducerFactory() {
        return new DefaultKafkaProducerFactory<>(this.producerConfigs);
    }

    @Bean
    public KafkaTemplate<String, BranchDto> branchDtoKafkaTemplate() {
        return new KafkaTemplate<>(branchDtoProducerFactory());
    }

    @Bean
    public NewTopic branchCreateTopic(@Value("${spring.kafka.topics.branch.create.name}") String topicName,
                                      @Value("${spring.kafka.topics.branch.create.partitions}") int partitions,
                                      @Value("${spring.kafka.topics.branch.create.replication-factor}") short factor) {
        return new NewTopic(topicName, partitions, factor);
    }

    @Bean
    public NewTopic branchUpdateTopic(@Value("${spring.kafka.topics.branch.update.name}") String topicName,
                                      @Value("${spring.kafka.topics.branch.update.partitions}") int partitions,
                                      @Value("${spring.kafka.topics.branch.update.replication-factor}") short factor) {
        return new NewTopic(topicName, partitions, factor);
    }

    @Bean
    public NewTopic branchDeleteTopic(@Value("${spring.kafka.topics.branch.delete.name}") String topicName,
                                      @Value("${spring.kafka.topics.branch.delete.partitions}") int partitions,
                                      @Value("${spring.kafka.topics.branch.delete.replication-factor}") short factor) {
        return new NewTopic(topicName, partitions, factor);
    }

    @Bean
    public NewTopic branchGetTopic(@Value("${spring.kafka.topics.branch.get.name}") String topicName,
                                   @Value("${spring.kafka.topics.branch.get.partitions}") int partitions,
                                   @Value("${spring.kafka.topics.branch.get.replication-factor}") short factor) {
        return new NewTopic(topicName, partitions, factor);
    }
}
