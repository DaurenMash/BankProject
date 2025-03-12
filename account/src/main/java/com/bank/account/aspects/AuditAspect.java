package com.bank.account.aspects;

import com.bank.account.dto.AuditDto;
import com.bank.account.entity.Account;
import com.bank.account.producers.AuditProducer;
import com.bank.account.service.AuditService;
import com.bank.account.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
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

    public AuditAspect(AuditService auditService, AuditProducer auditProducer) {
        this.auditService = auditService;
        this.auditProducer = auditProducer;
    }

    @After("execution(* com.bank.account.service.AccountServiceImpl.createNewAccount(com.bank.account.entity.Account))")
    public void logCreateAccount(JoinPoint joinPoint) {
        try {
            Account account = (Account) joinPoint.getArgs()[0];
            String entityJson = JsonUtils.convertToJson(account);

            AuditDto auditDto = auditService.setDataToAuditDtoForNewAudit(
                    ENTITY_TYPE,
                    "createNewAccount",
                    CURRENT_USER,
                    null,
                    new Timestamp(System.currentTimeMillis()),
                    null,
                    null,
                    entityJson);
            auditProducer.sendAuditLogEvent(auditDto);

            log.info("Successfully created new account {}", account.getAccountNumber());
        } catch (Exception e) {
            log.error("Failed to create new account {}", e.getMessage());
        }

    }

    @AfterReturning(pointcut = "execution(* com.bank.account.service.AccountServiceImpl.updateCurrentAccount(Long, " +
            "com.bank.account.entity.Account))", returning = "result")
    public void logUpdateCurrentAccount(JoinPoint joinPoint, Object result) {
        try {
            Long accountId = (Long) joinPoint.getArgs()[0];
            Account account = (Account) result;
            String newEntityJson = JsonUtils.convertToJson(account);

            String oldEntityJson;
            AuditDto oldAuditDto = auditService.getAuditByEntityId(accountId);

            if (oldAuditDto.getNewEntityJson() == null) {
                oldEntityJson = oldAuditDto.getEntityJson();
            } else {
                oldEntityJson = oldAuditDto.getNewEntityJson();
            }

            AuditDto auditDto = auditService.setDataToAuditDto(oldAuditDto,
                    "updateCurrentAccount",
                    CURRENT_USER,
                    new Timestamp(System.currentTimeMillis()),
                    newEntityJson,
                    oldEntityJson);
            auditProducer.sendAuditLogEvent(auditDto);

            log.info("Successfully updated account {}", account.getId());
        } catch (Exception e) {
            log.error("Failed to update account {}", e.getMessage());
        }
    }
}

