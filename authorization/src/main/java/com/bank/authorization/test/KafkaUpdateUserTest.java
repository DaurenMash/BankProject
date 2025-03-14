package com.bank.authorization.test;

import com.bank.authorization.dto.UserDto;
import com.bank.authorization.dto.KafkaRequest;
import com.bank.authorization.dto.KafkaResponse;
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

public class KafkaUpdateUserTest {

    private static String jwtToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiYXV0aG9yaXRpZXMiOlsiUk9MRV9BRE1JTiJdLCJpYXQiOjE3NDE5NDQzNDcsImV4cCI6MTc0MTk4MDM0N30.K-5CKDZgIwEGwz5BmBH_EuR2PBNCm5FVyxs3wxlPVxU";

    private static final String KAFKA_BOOTSTRAP_SERVERS = "localhost:9092";
    private static final String TOPIC_UPDATE_USER = "user.update";
    private static final String TOPIC_UPDATE_USER_RESPONSE = "user.update.response";
    private static final String GROUP_ID = "authorization-group";

    public static void main(String[] args) {
        // Настройки продюсера
        Properties producerProps = new Properties();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BOOTSTRAP_SERVERS);
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        Producer<String, KafkaRequest> producer = new KafkaProducer<>(producerProps);

        // Настройки консюмера
        Properties consumerProps = new Properties();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BOOTSTRAP_SERVERS);
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        consumerProps.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        Consumer<String, KafkaResponse> consumer = new KafkaConsumer<>(consumerProps);
        consumer.subscribe(Collections.singletonList(TOPIC_UPDATE_USER_RESPONSE));

        // Создание тестового объекта UserDto
        UserDto userDto = new UserDto();
        userDto.setId(13L);
        userDto.setRole("ROLE_USER");
        userDto.setProfileId(113L);
        userDto.setPassword("newPassword456");

        // Формирование KafkaRequest
        KafkaRequest request = new KafkaRequest();
        request.setRequestId(UUID.randomUUID().toString());
        request.setJwtToken(jwtToken);
        request.setPayload(userDto);

        // Отправка KafkaRequest
        ProducerRecord<String, KafkaRequest> record = new ProducerRecord<>(TOPIC_UPDATE_USER, request);
        producer.send(record);
        producer.flush();

        // Ожидание ответа
        ConsumerRecords<String, KafkaResponse> records = consumer.poll(Duration.ofSeconds(30));
        for (ConsumerRecord<String, KafkaResponse> consumerRecord : records) {
            KafkaResponse response = consumerRecord.value();
            if (response.getRequestId().equals(request.getRequestId())) {
                System.out.println("Received response: " + response);
                break;
            }
        }

        // Закрытие ресурсов
        producer.close();
        consumer.close();
    }
}
