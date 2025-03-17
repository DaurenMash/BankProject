package com.bank.authorization.test;

import com.bank.authorization.dto.KafkaRequest;
import com.bank.authorization.dto.KafkaResponse;
import com.bank.authorization.dto.UserDto;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.UUID;

public class KafkaGetUserTest {

    private static String jwtToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiYXV0aG9yaXRpZXMiOlsiUk9MRV9BRE1JTiJdLCJpYXQiOjE3NDIyMDMyMDksImV4cCI6MTc0MjIzOTIwOX0.U65enjOqNGKVOcpSqUpop10d5AV5Hb08Xt79UpV5fGc";

    private static final String KAFKA_BOOTSTRAP_SERVERS = "localhost:9092";
    private static final String TOPIC_GET_USER = "user.get";
    private static final String TOPIC_GET_USER_RESPONSE = "user.get.response";
    private static final String GROUP_ID = "authorization-group";

    public static void main(String[] args) {
        Properties producerProps = new Properties();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BOOTSTRAP_SERVERS);
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        Producer<String, KafkaRequest> producer = new KafkaProducer<>(producerProps);

        Properties consumerProps = new Properties();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BOOTSTRAP_SERVERS);
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        consumerProps.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        Consumer<String, KafkaResponse> consumer = new KafkaConsumer<>(consumerProps);
        consumer.subscribe(Collections.singletonList(TOPIC_GET_USER_RESPONSE));

        KafkaRequest request = new KafkaRequest();
        request.setRequestId(UUID.randomUUID().toString());
        request.setJwtToken(jwtToken);
        request.setPayload(100L);

        producer.send(new ProducerRecord<>(TOPIC_GET_USER, request));
        producer.flush();

        ConsumerRecords<String, KafkaResponse> records = consumer.poll(Duration.ofSeconds(30));
        for (ConsumerRecord<String, KafkaResponse> record : records) {
            if (record.value().getRequestId().equals(request.getRequestId())) {
                System.out.println("Received response: " + record.value());
                break;
            }
        }

        producer.close();
        consumer.close();
    }
}