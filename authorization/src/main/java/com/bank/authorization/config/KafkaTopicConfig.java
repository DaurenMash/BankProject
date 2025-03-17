package com.bank.authorization.config;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class KafkaTopicConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @PostConstruct
    public void createTopics() {
        Map<String, Object> configs = new HashMap<>();
        configs.put("bootstrap.servers", bootstrapServers);
        KafkaAdmin kafkaAdmin = new KafkaAdmin(configs);

        try (AdminClient adminClient = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {
            adminClient.createTopics(
                    List.of(
                            new NewTopic("auth.login", 1, (short) 1),
                            new NewTopic("auth.login.response", 1, (short) 1),
                            new NewTopic("user.create", 1, (short) 1),
                            new NewTopic("user.create.response", 1, (short) 1),
                            new NewTopic("user.update", 1, (short) 1),
                            new NewTopic("user.update.response", 1, (short) 1),
                            new NewTopic("user.delete", 1, (short) 1),
                            new NewTopic("user.delete.response", 1, (short) 1),
                            new NewTopic("user.get", 1, (short) 1),
                            new NewTopic("user.get.response", 1, (short) 1),
                            new NewTopic("user.get.all", 1, (short) 1),
                            new NewTopic("user.get.all.response", 1, (short) 1),
                            new NewTopic("error.responses", 1, (short) 1)
                    )
            );
        }
    }
}