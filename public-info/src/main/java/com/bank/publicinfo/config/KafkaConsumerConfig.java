package com.bank.publicinfo.config;

import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConsumerConfig {

    private static final String BOOTSTRAP_SERVERS = "bootstrap.servers";
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;
    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @Bean
    public Map<String, Object> consumerConfigs() {
        final Map<String, Object> props = new HashMap<>();
        props.put(BOOTSTRAP_SERVERS, this.bootstrapServers);
        props.put("group.id", this.groupId);
        props.put("key.deserializer", StringDeserializer.class);
        props.put("value.deserializer", ErrorHandlingDeserializer.class);
        props.put("spring.deserializer.value.delegate.class", JsonDeserializer.class);
        return props;
    }

}
