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
        final Map<String, Object> configs = new HashMap<>();
        configs.put("bootstrap.servers", bootstrapServers);
        final KafkaAdmin kafkaAdmin = new KafkaAdmin(configs);

        try (AdminClient adminClient = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {
            adminClient.createTopics(
                    List.of(
                            //Следующие два топика это для обработки процесса логина и получения нового токена
                            new NewTopic("auth.login", 1, (short) 1),
                            new NewTopic("auth.login.response", 1, (short) 1),
                            //Следующие два топика это для проверки валидности токенов уже в процессе работы
                            new NewTopic("auth.validate", 1, (short) 1),
                            new NewTopic("auth.validate.response", 1, (short) 1),
                            //Следующие два топика это для запросов на создание нового пользователя
                            new NewTopic("user.create", 1, (short) 1),
                            new NewTopic("user.create.response", 1, (short) 1),
                            //Следующие два топика это для запросов на обновление пользователя
                            new NewTopic("user.update", 1, (short) 1),
                            new NewTopic("user.update.response", 1, (short) 1),
                            //Следующие два топика это для запросов на удаление пользователя
                            new NewTopic("user.delete", 1, (short) 1),
                            new NewTopic("user.delete.response", 1, (short) 1),
                            //Следующие два топика это для запросов на получение информации о пользователе
                            new NewTopic("user.get", 1, (short) 1),
                            new NewTopic("user.get.response", 1, (short) 1),
                            //Следующие два топика это для запросов на получение информации о всех пользователях
                            new NewTopic("user.get.all", 1, (short) 1),
                            new NewTopic("user.get.all.response", 1, (short) 1),
                            //Следующий топик это для логирования ошибок в процессе работы микросервиса
                            new NewTopic("error.logging", 1, (short) 1)
                    )
            );
        }
    }
}
