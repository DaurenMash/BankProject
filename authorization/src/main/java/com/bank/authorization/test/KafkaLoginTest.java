package com.bank.authorization.test;

import com.bank.authorization.dto.AuthRequest;
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
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.UUID;

public class KafkaLoginTest {

    private static final String KAFKA_BOOTSTRAP_SERVERS = "localhost:9092";
    private static final String TOPIC_AUTH_LOGIN = "auth.login";
    private static final String TOPIC_AUTH_LOGIN_RESPONSE = "auth.login.response";
    private static final String GROUP_ID = "authorization-group";

    public static void main(String[] args) {
        // Создаем KafkaProducer для отправки AuthRequest
        Properties producerProps = new Properties();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BOOTSTRAP_SERVERS);
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        Producer<String, AuthRequest> producer = new KafkaProducer<>(producerProps);

        // Создаем KafkaConsumer для получения ответа
        Properties consumerProps = new Properties();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BOOTSTRAP_SERVERS);
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        consumerProps.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        Consumer<String, KafkaResponse> consumer = new KafkaConsumer<>(consumerProps);
        consumer.subscribe(Collections.singletonList(TOPIC_AUTH_LOGIN_RESPONSE));

        // Отправляем AuthRequest
        final String requestId = UUID.randomUUID().toString();
        AuthRequest authRequest = new AuthRequest(requestId,1L, "admin123");
        ProducerRecord<String, AuthRequest> record = new ProducerRecord<>(TOPIC_AUTH_LOGIN, authRequest);
        producer.send(record);
        producer.flush();

        // Ожидаем ответ
        ConsumerRecords<String, KafkaResponse> records = consumer.poll(Duration.ofSeconds(30));
        for (ConsumerRecord<String, KafkaResponse> consumerRecord : records) {
            KafkaResponse response = consumerRecord.value();
            if (response.getRequestId().equals(requestId)) {
                System.out.println("Received response: " + response);
                break;
            }
        }

        // Закрываем producer и consumer
        producer.close();
        consumer.close();
    }
}