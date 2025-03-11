package com.bank.account.aspects;

import com.bank.account.dto.AuditDto;
import com.bank.account.entity.Account;
import com.bank.account.producers.AuditProducer;
import com.bank.account.service.AuditService;
import com.bank.account.utils.JsonUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

@Aspect
@Component
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
        Account account = (Account) joinPoint.getArgs()[0];
        String entityJson = JsonUtils.convertToJson(account);

        AuditDto auditDto = auditService.setDataToAuditDtoForAspect(
                ENTITY_TYPE,
                "createNewAccount",
                CURRENT_USER,
                null,
                new Timestamp(System.currentTimeMillis()),
                null,
                null,
                entityJson,
                account.getId());
        auditProducer.sendAuditLogEvent(auditDto);
    }

    @AfterReturning(pointcut = "execution(* com.bank.account.service.AccountServiceImpl.updateCurrentAccount(Long, " +
            "com.bank.account.entity.Account))", returning = "result")
    public void logUpdateCurrentAccount(JoinPoint joinPoint, Object result) {
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

        AuditDto auditDto = auditService.setDataToAuditDtoForAspect(
                ENTITY_TYPE,
                "updateNewAccount",
                CURRENT_USER,
                CURRENT_USER,
                Timestamp.valueOf(oldAuditDto.getCreatedBy()),
                new Timestamp(System.currentTimeMillis()),
                newEntityJson,
                oldEntityJson,
                account.getId());

        auditProducer.sendAuditLogEvent(auditDto);
    }
}
