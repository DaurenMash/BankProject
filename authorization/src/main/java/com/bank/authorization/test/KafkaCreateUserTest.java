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
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.UUID;

public class KafkaCreateUserTest {

    private static String jwtToken;

    private static final String KAFKA_BOOTSTRAP_SERVERS = "localhost:9092";
    private static final String TOPIC_CREATE_USER = "user.create";
    private static final String TOPIC_CREATE_USER_RESPONSE = "user.create.response";
    private static final String GROUP_ID = "authorization-group";
    private static final Long TIMEOUT = 30L;
    private static final Long SET_PROFILE_ID = 88L;


    static {
        loadConfig();
    }

    private static void loadConfig() {
        final Properties properties = new Properties();
        try (InputStream input = KafkaCreateUserTest.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input != null) {
                properties.load(input);
                jwtToken = properties.getProperty("jwt.token");
            } else {
                throw new RuntimeException("Файл config.properties не найден!");
            }
        } catch (IOException e) {
            throw new RuntimeException("Ошибка загрузки конфигурации", e);
        }
    }
    public static void main(String[] args) {

        final Properties producerProps = new Properties();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BOOTSTRAP_SERVERS);
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        final Producer<String, KafkaRequest> producer = new KafkaProducer<>(producerProps);

        final Properties consumerProps = new Properties();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BOOTSTRAP_SERVERS);
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        consumerProps.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        final Consumer<String, KafkaResponse> consumer = new KafkaConsumer<>(consumerProps);
        consumer.subscribe(Collections.singletonList(TOPIC_CREATE_USER_RESPONSE));

        final UserDto userDto = new UserDto();
        userDto.setRole("ROLE_USER");
        userDto.setProfileId(SET_PROFILE_ID);
        userDto.setPassword("password123");

        final KafkaRequest request = new KafkaRequest();
        request.setRequestId(UUID.randomUUID().toString());
        request.setJwtToken(jwtToken);
        request.setPayload(userDto);

        final ProducerRecord<String, KafkaRequest> record = new ProducerRecord<>(TOPIC_CREATE_USER, request);
        producer.send(record);
        producer.flush();

        final ConsumerRecords<String, KafkaResponse> records = consumer.poll(Duration.ofSeconds(TIMEOUT));
        for (ConsumerRecord<String, KafkaResponse> consumerRecord : records) {
            final KafkaResponse response = consumerRecord.value();
            if (response.getRequestId().equals(request.getRequestId())) {
                System.out.println("Received response: " + response);
                break;
            }
        }

        producer.close();
        consumer.close();
    }
}
