package com.bank.account.consumers;

import com.bank.account.config.KafkaTopicsConfig;
import com.bank.account.dto.AccountDto;
import com.bank.account.exception.KafkaErrorSender;
import com.bank.account.producers.AccountProducer;
import com.bank.account.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountQueryConsumer {

    @Value("${spring.kafka.consumer.group-ids.account}")
    private String accountGroup;

    @Value("${kafka.topics.error-logs}")
    private String topicError;

    private final AccountService accountService;
    private final AccountProducer accountProducer;
    private final KafkaErrorSender kafkaErrorSender;
    private final KafkaTopicsConfig kafkaTopicsConfig;

    @KafkaListener(topics = "${kafka.topics.account-get}", groupId = "@accountQueryConsumer.accountGroup")
    public void handleGetAccounts(@Header("Authorization") String jwtToken) {
        try {
            validateJwtOrThrow(jwtToken);

            final List<AccountDto> accountsDto = accountService.getAllAccounts();
            accountProducer.sendAccountList(kafkaTopicsConfig.getExternalAccountGet(), accountsDto);

            log.info("Method 'handleGetAccounts' completed successful. All accounts sent");
        } catch (Exception e) {
            log.error("Method 'handleGetAccounts' failed: ", e);
            kafkaErrorSender.sendError(e, topicError);
        }
    }

    @KafkaListener(topics = "${kafka.topics.account-get-by-id}", groupId = "@accountQueryConsumer.accountGroup",
            containerFactory = "longKafkaListenerContainerFactory")
    public void handleGetByIdAccount(@Payload Long accountId, @Header("Authorization") String jwtToken) {
        try {
            validateJwtOrThrow(jwtToken);

            final AccountDto resultAccount = accountService.getAccountById(accountId);

            accountProducer.sendExternalEvent(kafkaTopicsConfig.getExternalAccountGetById(), resultAccount);

            log.info("Method 'handleGetByIdAccount' completed successful. Account found: {}", resultAccount);
        } catch (Exception e) {
            log.error("Method 'handleGetByIdAccount' failed: ", e);
            kafkaErrorSender.sendError(e, topicError);
        }
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
