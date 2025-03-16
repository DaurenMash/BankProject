package com.bank.authorization.test;

import com.bank.authorization.dto.UserDto;
import com.bank.authorization.dto.KafkaRequest;
import com.bank.authorization.dto.KafkaResponse;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.UUID;

public class KafkaCreateUserTest {

    private static String jwtToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiYXV0aG9yaXRpZXMiOlsiUk9MRV9BRE1JTiJdLCJpYXQiOjE3NDIxMjExMDEsImV4cCI6MTc0MjE1NzEwMX0.qdMWpU4JzIBIl_GJZCwymLO2WlYgeXc82XNq8FbbvhU";

    private static final String KAFKA_BOOTSTRAP_SERVERS = "localhost:9092";
    private static final String TOPIC_CREATE_USER = "user.create";
    private static final String TOPIC_CREATE_USER_RESPONSE = "user.create.response";
    private static final String GROUP_ID = "authorization-group";

    public static void main(String[] args) {
        // Создаем KafkaProducer для отправки KafkaRequest
        Properties producerProps = new Properties();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BOOTSTRAP_SERVERS);
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        Producer<String, KafkaRequest> producer = new KafkaProducer<>(producerProps);

        // Создаем KafkaConsumer для получения ответа
        Properties consumerProps = new Properties();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BOOTSTRAP_SERVERS);
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        consumerProps.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        Consumer<String, KafkaResponse> consumer = new KafkaConsumer<>(consumerProps);
        consumer.subscribe(Collections.singletonList(TOPIC_CREATE_USER_RESPONSE));

        // Создаем тестовый объект UserDto
        UserDto userDto = new UserDto();
        userDto.setRole("ROLE_USER");
        userDto.setProfileId(4L);
        userDto.setPassword("password123");

        // Создаем KafkaRequest для отправки
        KafkaRequest request = new KafkaRequest();
        request.setRequestId(UUID.randomUUID().toString());
        request.setJwtToken(jwtToken);
        request.setPayload(userDto);

        // Отправляем KafkaRequest
        ProducerRecord<String, KafkaRequest> record = new ProducerRecord<>(TOPIC_CREATE_USER, request);
        producer.send(record);
        producer.flush();

        // Ожидаем ответ
        ConsumerRecords<String, KafkaResponse> records = consumer.poll(Duration.ofSeconds(30));
        for (ConsumerRecord<String, KafkaResponse> consumerRecord : records) {
            KafkaResponse response = consumerRecord.value();
            if (response.getRequestId().equals(request.getRequestId())) {
                System.out.println("Received response: " + response);
                break;
            }
        }

        // Закрываем producer и consumer
        producer.close();
        consumer.close();
    }
}