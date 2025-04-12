package com.bank.publicinfo.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    private NewTopic createTopic(String name, int partitions, short replicationFactor) {
        return new NewTopic(name, partitions, replicationFactor);
    }

    @Bean
    public NewTopic errorLogsTopic(@Value("${spring.kafka.topics.error-log.name}") String topicName,
                                   @Value("${spring.kafka.topics.error-log.partitions}") int partitions,
                                   @Value("${spring.kafka.topics.error-log.replication-factor}") short factor) {
        return createTopic(topicName, partitions, factor);
    }

    // ATM Topics
    @Bean
    public NewTopic atmCreateTopic(@Value("${spring.kafka.topics.atm.create.name}") String topicName,
                                   @Value("${spring.kafka.topics.atm.create.partitions}") int partitions,
                                   @Value("${spring.kafka.topics.atm.create.replication-factor}") short factor) {
        return createTopic(topicName, partitions, factor);
    }

    @Bean
    public NewTopic atmUpdateTopic(@Value("${spring.kafka.topics.atm.update.name}") String topicName,
                                   @Value("${spring.kafka.topics.atm.update.partitions}") int partitions,
                                   @Value("${spring.kafka.topics.atm.update.replication-factor}") short factor) {
        return createTopic(topicName, partitions, factor);
    }

    @Bean
    public NewTopic atmDeleteTopic(@Value("${spring.kafka.topics.atm.delete.name}") String topicName,
                                   @Value("${spring.kafka.topics.atm.delete.partitions}") int partitions,
                                   @Value("${spring.kafka.topics.atm.delete.replication-factor}") short factor) {
        return createTopic(topicName, partitions, factor);
    }

    @Bean
    public NewTopic atmGetTopic(@Value("${spring.kafka.topics.atm.get.name}") String topicName,
                                @Value("${spring.kafka.topics.atm.get.partitions}") int partitions,
                                @Value("${spring.kafka.topics.atm.get.replication-factor}") short factor) {
        return createTopic(topicName, partitions, factor);
    }

    // Bank Topics
    @Bean
    public NewTopic bankCreateTopic(@Value("${spring.kafka.topics.bank.create.name}") String topicName,
                                    @Value("${spring.kafka.topics.bank.create.partitions}") int partitions,
                                    @Value("${spring.kafka.topics.bank.create.replication-factor}") short factor) {
        return createTopic(topicName, partitions, factor);
    }

    @Bean
    public NewTopic bankUpdateTopic(@Value("${spring.kafka.topics.bank.update.name}") String topicName,
                                    @Value("${spring.kafka.topics.bank.update.partitions}") int partitions,
                                    @Value("${spring.kafka.topics.bank.update.replication-factor}") short factor) {
        return createTopic(topicName, partitions, factor);
    }

    @Bean
    public NewTopic bankDeleteTopic(@Value("${spring.kafka.topics.bank.delete.name}") String topicName,
                                    @Value("${spring.kafka.topics.bank.delete.partitions}") int partitions,
                                    @Value("${spring.kafka.topics.bank.delete.replication-factor}") short factor) {
        return createTopic(topicName, partitions, factor);
    }

    @Bean
    public NewTopic bankGetTopic(@Value("${spring.kafka.topics.bank.get.name}") String topicName,
                                 @Value("${spring.kafka.topics.bank.get.partitions}") int partitions,
                                 @Value("${spring.kafka.topics.bank.get.replication-factor}") short factor) {
        return createTopic(topicName, partitions, factor);
    }

    //Branch Topics
    @Bean
    public NewTopic branchCreateTopic(@Value("${spring.kafka.topics.branch.create.name}") String topicName,
                                      @Value("${spring.kafka.topics.branch.create.partitions}") int partitions,
                                      @Value("${spring.kafka.topics.branch.create.replication-factor}") short factor) {
        return createTopic(topicName, partitions, factor);
    }

    @Bean
    public NewTopic branchUpdateTopic(@Value("${spring.kafka.topics.branch.update.name}") String topicName,
                                      @Value("${spring.kafka.topics.branch.update.partitions}") int partitions,
                                      @Value("${spring.kafka.topics.branch.update.replication-factor}") short factor) {
        return createTopic(topicName, partitions, factor);
    }

    @Bean
    public NewTopic branchDeleteTopic(@Value("${spring.kafka.topics.branch.delete.name}") String topicName,
                                      @Value("${spring.kafka.topics.branch.delete.partitions}") int partitions,
                                      @Value("${spring.kafka.topics.branch.delete.replication-factor}") short factor) {
        return createTopic(topicName, partitions, factor);
    }

    @Bean
    public NewTopic branchGetTopic(@Value("${spring.kafka.topics.branch.get.name}") String topicName,
                                   @Value("${spring.kafka.topics.branch.get.partitions}") int partitions,
                                   @Value("${spring.kafka.topics.branch.get.replication-factor}") short factor) {
        return createTopic(topicName, partitions, factor);
    }

    // Certificate Topics
    @Bean
    public NewTopic certificateCreateTopic(
            @Value("${spring.kafka.topics.certificate.create.name}") String topicName,
            @Value("${spring.kafka.topics.certificate.create.partitions}") int partitions,
            @Value("${spring.kafka.topics.certificate.create.replication-factor}") short factor) {
        return createTopic(topicName, partitions, factor);
    }

    @Bean
    public NewTopic certificateUpdateTopic(
            @Value("${spring.kafka.topics.certificate.update.name}") String topicName,
            @Value("${spring.kafka.topics.certificate.update.partitions}") int partitions,
            @Value("${spring.kafka.topics.certificate.update.replication-factor}") short factor) {
        return createTopic(topicName, partitions, factor);
    }

    @Bean
    public NewTopic certificateDeleteTopic(
            @Value("${spring.kafka.topics.certificate.delete.name}") String topicName,
            @Value("${spring.kafka.topics.certificate.delete.partitions}") int partitions,
            @Value("${spring.kafka.topics.certificate.delete.replication-factor}") short factor) {
        return createTopic(topicName, partitions, factor);
    }

    @Bean
    public NewTopic certificateGetTopic(
            @Value("${spring.kafka.topics.certificate.get.name}") String topicName,
            @Value("${spring.kafka.topics.certificate.get.partitions}") int partitions,
            @Value("${spring.kafka.topics.certificate.get.replication-factor}") short factor) {
        return createTopic(topicName, partitions, factor);
    }

    //License Topics
    @Bean
    public NewTopic licenseDtoCreateTopic(
            @Value("${spring.kafka.topics.license.create.name}") String topicName,
            @Value("${spring.kafka.topics.license.create.partitions}") int partitions,
            @Value("${spring.kafka.topics.license.create.replication-factor}") short factor) {
        return createTopic(topicName, partitions, factor);
    }

    @Bean
    public NewTopic licenseDtoUpdateTopic(
            @Value("${spring.kafka.topics.license.update.name}") String topicName,
            @Value("${spring.kafka.topics.license.update.partitions}") int partitions,
            @Value("${spring.kafka.topics.license.update.replication-factor}") short factor) {
        return createTopic(topicName, partitions, factor);
    }

    @Bean
    public NewTopic licenseDtoDeleteTopic(
            @Value("${spring.kafka.topics.license.delete.name}") String topicName,
            @Value("${spring.kafka.topics.license.delete.partitions}") int partitions,
            @Value("${spring.kafka.topics.license.delete.replication-factor}") short factor) {
        return createTopic(topicName, partitions, factor);
    }

    @Bean
    public NewTopic licenseDtoGetTopic(
            @Value("${spring.kafka.topics.license.get.name}") String topicName,
            @Value("${spring.kafka.topics.license.get.partitions}") int partitions,
            @Value("${spring.kafka.topics.license.get.replication-factor}") short replicationFactor) {
        return createTopic(topicName, partitions, replicationFactor);
    }
}
