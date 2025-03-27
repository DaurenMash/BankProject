package com.bank.account.consumers;

import com.bank.account.config.KafkaTopicsConfig;
import com.bank.account.dto.AccountDto;
import com.bank.account.exception.GlobalExceptionHandler;
import com.bank.account.producers.AccountProducer;
import com.bank.account.service.AccountService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountConsumer {

    @Value("${spring.kafka.consumer.group-ids.account}")
    private String accountGroup;

    @Value("${kafka.topics.error-logs}")
    private static final String TOPIC_ERROR = "error.logs";

    private final ObjectMapper objectMapper;
    private final AccountService accountService;
    private final AccountProducer accountProducer;
    private final GlobalExceptionHandler globalExceptionHandler;
    private final KafkaTopicsConfig kafkaTopicsConfig;

    @KafkaListener (topics = "${kafka.topics.account-create}", groupId = "${accountGroup}")
    public void handleCreateAccount(@Payload String json, @Header("Authorization") String jwtToken) {
        try {
            validateJwtOrThrow(jwtToken);

            final AccountDto accountDto = objectMapper.readValue(json, AccountDto.class);
            final AccountDto responseAccount = accountService.createNewAccount(accountDto);

            accountProducer.sendExternalEvent(kafkaTopicsConfig.getExternalAccountCreate(), responseAccount);

            log.info("Method 'handleCreateAccount' completed successful");
        } catch (Exception e) {
            log.error("Method 'handleCreateAccount' failed: ", e);
            globalExceptionHandler.handleException(e, TOPIC_ERROR);
        }
    }

    @KafkaListener(topics = "${kafka.topics.account-update}", groupId = "${accountGroup}")
    public void handleUpdateAccount(@Payload String json, @Header("Authorization") String jwtToken) {
        try {
            validateJwtOrThrow(jwtToken);

            final AccountDto accountDto = objectMapper.readValue(json, AccountDto.class);
            final AccountDto responseAccount = accountService.updateCurrentAccount(accountDto.getId(), accountDto);

            accountProducer.sendExternalEvent(kafkaTopicsConfig.getExternalAccountUpdate(), responseAccount);

            log.info("Method 'handleUpdateAccount' completed  successful ");
        } catch (Exception e) {
            log.error("Method 'handleUpdateAccount' failed: ", e);
            globalExceptionHandler.handleException(e, TOPIC_ERROR);
        }
    }

    @KafkaListener(topics = "${kafka.topics.account-delete}", groupId = "${accountGroup}")
    public void handleDeleteAccount(@Payload String json, @Header("Authorization") String jwtToken) {
        try {
            validateJwtOrThrow(jwtToken);

            final Long accountId = parseJsonForId(json);

            accountService.deleteAccount(accountId);
            accountProducer.sendExternalEvent(kafkaTopicsConfig.getExternalAccountDelete(),
                    String.format("Account with id: %s successfully deleted", accountId));

            log.info("Method 'handleDeleteAccount' completed successful ");
        } catch (Exception e) {
            log.error("Method 'handleDeleteAccount' failed: ", e);
            globalExceptionHandler.handleException(e, TOPIC_ERROR);
        }
    }

    @KafkaListener(topics = "${kafka.topics.account-get}", groupId = "${accountGroup}")
    public void handleGetAccounts(@Header("Authorization") String jwtToken) {
        try {
            validateJwtOrThrow(jwtToken);

            final List<AccountDto> accountsDto = accountService.getAllAccounts();
            accountProducer.sendExternalEvent(kafkaTopicsConfig.getExternalAccountGet(), accountsDto);

            log.info("Method 'handleGetAccounts' completed successful. All accounts sent");
        } catch (Exception e) {
            log.error("Method 'handleGetAccounts' failed: ", e);
            globalExceptionHandler.handleException(e, TOPIC_ERROR);
        }
    }

    @KafkaListener(topics = "${kafka.topics.account-get-by-id}", groupId = "${accountGroup}")
    public void handleGetByIdAccount(@Payload String json, @Header("Authorization") String jwtToken) {
        try {
            validateJwtOrThrow(jwtToken);

            final Long accountId = parseJsonForId(json);
            final AccountDto resultAccount = accountService.getAccountById(accountId);

            accountProducer.sendExternalEvent(kafkaTopicsConfig.getExternalAccountGetById(), resultAccount);

            log.info("Method 'handleGetByIdAccount' completed successful. Account found: {}", resultAccount);
        } catch (Exception e) {
            log.error("Method 'handleGetByIdAccount' failed: ", e);
            globalExceptionHandler.handleException(e, TOPIC_ERROR);
        }
    }

    private Long parseJsonForId(String json) throws JsonProcessingException {
        final Long accountId;
        if (json.matches("\\d+")) {
            accountId = Long.parseLong(json);
        } else {
            final Map<String, Object> map = objectMapper.readValue(json, Map.class);
            accountId = Long.valueOf(map.get("id").toString());
        }
        return accountId;
    }

    private boolean validateToken(String jwtToken) {
        return true;
    }

    private void validateJwtOrThrow(String jwtToken) {
        if (!validateToken(jwtToken)) {
            throw new SecurityException("Invalid JWT");
        }
    }
}
