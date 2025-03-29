package com.bank.account.consumers;

import com.bank.account.config.KafkaTopicsConfig;
import com.bank.account.dto.AccountDto;
import com.bank.account.exception.KafkaErrorSender;
import com.bank.account.producers.AccountProducer;
import com.bank.account.security.TokenValidationService;
import com.bank.account.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountCommandConsumer {

    @Value("${spring.kafka.consumer.group-ids.account}")
    private String accountGroup;

    @Value("${kafka.topics.error-logs}")
    private String topicError;

    private final AccountService accountService;
    private final AccountProducer accountProducer;
    private final KafkaErrorSender kafkaErrorSender;
    private final KafkaTopicsConfig kafkaTopicsConfig;
    private final TokenValidationService tokenValidationService;

    @KafkaListener (topics = "${kafka.topics.account-create}", groupId = "@accountCommandConsumer.accountGroup",
            containerFactory = "accountKafkaListenerContainerFactory")
    public void handleCreateAccount(@Payload AccountDto accountDto, @Header("Authorization") String jwtToken) {
        try {
            tokenValidationService.validateJwtOrThrow(jwtToken);

            final AccountDto responseAccount = accountService.createNewAccount(accountDto);

            accountProducer.sendExternalEvent(kafkaTopicsConfig.getExternalAccountCreate(), responseAccount);

            log.info("Method 'handleCreateAccount' completed successful");
        } catch (Exception e) {
            log.error("Method 'handleCreateAccount' failed: ", e);
            kafkaErrorSender.sendError(e, topicError);
        }
    }

    @KafkaListener(topics = "${kafka.topics.account-update}", groupId = "@accountCommandConsumer.accountGroup",
            containerFactory = "accountKafkaListenerContainerFactory")
    public void handleUpdateAccount(@Payload AccountDto accountDto, @Header("Authorization") String jwtToken) {
        try {
            tokenValidationService.validateJwtOrThrow(jwtToken);

            final AccountDto responseAccount = accountService.updateCurrentAccount(accountDto.getId(), accountDto);

            accountProducer.sendExternalEvent(kafkaTopicsConfig.getExternalAccountUpdate(), responseAccount);

            log.info("Method 'handleUpdateAccount' completed  successful ");
        } catch (Exception e) {
            log.error("Method 'handleUpdateAccount' failed: ", e);
            kafkaErrorSender.sendError(e, topicError);
        }
    }

    @KafkaListener(topics = "${kafka.topics.account-delete}", groupId = "@accountCommandConsumer.accountGroup",
            containerFactory = "longKafkaListenerContainerFactory")
    public void handleDeleteAccount(@Payload Long accountId, @Header("Authorization") String jwtToken) {
        try {
            tokenValidationService.validateJwtOrThrow(jwtToken);

            accountService.deleteAccount(accountId);
            accountProducer.sendTextMessage(kafkaTopicsConfig.getExternalAccountDelete(),
                    String.format("Account with id: %s successfully deleted", accountId));

            log.info("Method 'handleDeleteAccount' completed successful ");
        } catch (Exception e) {
            log.error("Method 'handleDeleteAccount' failed: ", e);
            kafkaErrorSender.sendError(e, topicError);
        }
    }
}
