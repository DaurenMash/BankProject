package com.bank.profile.config;

import com.bank.profile.util.KafkaTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicsConfig {

    @Bean
    public KafkaTopic kafkaTopic()
    {
        return new KafkaTopic();
    }
}
