package com.bank.account.producers;

import com.bank.account.config.KafkaTopicsConfig;
import com.bank.account.dto.AccountDto;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountProducer {
    private static final String HEADER_AUTHORIZATION = "Authorization";

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaTopicsConfig kafkaTopicsConfig;

    public void sendCreatedAccountEvent(String json, String jwtToken) {
        sendMessageToTopic(json, kafkaTopicsConfig.getAccountCreate(), jwtToken,
                "Created account event", "account creation");
    }

    public void sendUpdatedAccountEvent(String json, String jwtToken) {
        sendMessageToTopic(json, kafkaTopicsConfig.getAccountUpdate(), jwtToken,
                "Updated account event", "account update");
    }

    public void sendDeletedAccountEvent(String json, String jwtToken) {
        sendMessageToTopic(json, kafkaTopicsConfig.getAccountDelete(), jwtToken,
                "Deleted account event", "account delete");
    }

    public void sendGetAccountsEvent(String jwtToken) {
        sendMessageToTopic("", kafkaTopicsConfig.getAccountGet(), jwtToken,
                "Get accounts event", "accounts get");
    }

    public void sendGetOneAccountByIdEvent(String json, String jwtToken) {
        sendMessageToTopic(json, kafkaTopicsConfig.getAccountGetById(), jwtToken,
                "Get account by ID event", "account get by ID");
    }

    private void sendMessageToTopic(Object json, String topic, String jwtToken,
                                    String successMessage, String errorContext) {
        try {
            final Message<Object> message = MessageBuilder
                    .withPayload(json)
                    .setHeader(KafkaHeaders.TOPIC, topic)
                    .setHeader(HEADER_AUTHORIZATION, jwtToken)
                    .build();
            kafkaTemplate.send(message);
            log.info("{} successfully finished.", successMessage);
        } catch (Exception e) {
            log.error("Failed to send {} event: ", errorContext, e);
        }
    }

    public void sendExternalEvent(String topic, Object payload) {
        try {
            kafkaTemplate.send(topic, payload);
            log.info("External event. Successfully sent event to topic: {}", topic);
        } catch (Exception e) {
            log.error("External Event. Failed to send event to topic {}:", topic, e);
        }
    }
}
