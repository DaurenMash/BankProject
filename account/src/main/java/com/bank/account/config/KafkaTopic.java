package com.bank.account.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopic {

    @Bean
    public NewTopic accountCreateTopic() {
        return TopicBuilder.name("account.create").build();
    }

    @Bean
    public NewTopic accountUpdateTopic() {
        return TopicBuilder.name("account.update").build();
    }

    @Bean
    public NewTopic accountDeleteTopic() {
        return TopicBuilder.name("account.delete").build();
    }

    @Bean
    public NewTopic accountGetTopic() {
        return TopicBuilder.name("account.get").build();
    }

    @Bean
    public NewTopic accountListTopic() {
        return TopicBuilder.name("audit.logs").build();
    }
}
