package com.bank.account.aspects;

import com.bank.account.dto.AccountDto;
import com.bank.account.dto.AuditDto;
import com.bank.account.producers.AuditProducer;
import com.bank.account.service.AuditService;
import com.bank.account.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

@Aspect
@Component
@Slf4j
public class AuditAspect {
    private final AuditService auditService;
    private final AuditProducer auditProducer;
    private final String ENTITY_TYPE = "Account";
    private final String CURRENT_USER = "SYSTEM";
    private final String CREATE_OPERATION = "CREATION";
    private final String UPDATE_OPERATION = "UPDATE";

    public AuditAspect(AuditService auditService, AuditProducer auditProducer) {
        this.auditService = auditService;
        this.auditProducer = auditProducer;
    }

    @AfterReturning(pointcut = "execution(* com.bank.account.service.AccountServiceImpl.createNewAccount(..))",
            returning = "result")
    public void logCreateAccount(JoinPoint joinPoint, Object result) {
        try {
            AccountDto accountDto = (AccountDto) result;
            String entityJson = JsonUtils.convertToJson(accountDto);

            AuditDto auditDto = auditService.setDataToAuditDtoForNewAudit(
                    ENTITY_TYPE,
                    CREATE_OPERATION,
                    CURRENT_USER,
                    null,
                    new Timestamp(System.currentTimeMillis()),
                    null,
                    null,
                    entityJson);
            auditProducer.sendAuditLogEvent(auditDto);

            log.info("Successfully created new account {}", accountDto.getAccountNumber());
        } catch (Exception e) {
            log.error("Failed to create new account {}", e.getMessage());
        }

    }

    @AfterReturning(pointcut = "execution(* com.bank.account.service.AccountServiceImpl.updateCurrentAccount(Long,..))",
            returning = "result")
    public void logUpdateCurrentAccount(JoinPoint joinPoint, Object result) {
        try {
            Long accountId = (Long) joinPoint.getArgs()[0];
            AccountDto accountDto = (AccountDto) result;
            String newEntityJson = JsonUtils.convertToJson(accountDto);

            String oldEntityJson;
            AuditDto oldAuditDto = auditService.getAuditByEntityId(accountId);

            if (oldAuditDto.getNewEntityJson() == null) {
                oldEntityJson = oldAuditDto.getEntityJson();
            } else {
                oldEntityJson = oldAuditDto.getNewEntityJson();
            }

            AuditDto auditDto = auditService.setDataToAuditDto(oldAuditDto,
                    UPDATE_OPERATION,
                    CURRENT_USER,
                    new Timestamp(System.currentTimeMillis()),
                    newEntityJson,
                    oldEntityJson);
            auditProducer.sendAuditLogEvent(auditDto);

            log.info("Successfully updated account {}", accountDto.getAccountNumber());
        } catch (Exception e) {
            log.error("Failed to update account {}", e.getMessage());
        }
    }
}

