package com.bank.account.aspects;

import com.bank.account.dto.AuditDto;
import com.bank.account.entity.Account;
import com.bank.account.service.AccountService;
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
    private final AccountService accountService;
    private final String ENTITY_TYPE = "Account";
    private final String CURRENT_USER = "SYSTEM";

    public AuditAspect(AuditService auditService, AccountService accountService) {
        this.auditService = auditService;
        this.accountService = accountService;
    }

    @After("execution(* com.bank.account.service.AccountServiceImpl.createNewAccount(com.bank.account.entity.Account))")
    public void logCreateAccount(JoinPoint joinPoint) {
        System.out.println("логирование сохранения вызывается");
        Account account = (Account) joinPoint.getArgs()[0];
        String entityJson = JsonUtils.convertToJson(account);
        AuditDto auditDto = new AuditDto();

        auditDto.setEntityJson(entityJson);
        auditDto.setOperationType("createNewAccount");
        auditDto.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        auditDto.setEntityType(ENTITY_TYPE);
        auditDto.setCreatedBy(CURRENT_USER);

        System.out.println(account.getId());

        auditDto.setEntityId(account.getId());

        auditService.logAudit(auditDto);
    }

    @AfterReturning(pointcut = "execution(* com.bank.account.service.AccountServiceImpl.updateCurrentAccount(Long, " +
            "com.bank.account.entity.Account))", returning = "result")
    public void logUpdateCurrentAccount(JoinPoint joinPoint, Object result) {
        System.out.println("логирование изменения вызывается");
        String oldEntityJson;

        Long accountId = (Long) joinPoint.getArgs()[0];
        Account account = (Account) result;

        AuditDto oldAuditDto = auditService.getAuditByEntityId(accountId);

        AuditDto auditDto = new AuditDto();
        auditDto.setOperationType("updateCurrentAccount");
        auditDto.setModifiedAt(new Timestamp(System.currentTimeMillis()));
        auditDto.setEntityType(ENTITY_TYPE);
        auditDto.setModifiedBy(CURRENT_USER);

        if (oldAuditDto.getNewEntityJson() == null) {
            oldEntityJson = oldAuditDto.getEntityJson();
        } else {
            oldEntityJson = oldAuditDto.getNewEntityJson();
        }

        String newEntityJson = JsonUtils.convertToJson(account);

        auditDto.setNewEntityJson(newEntityJson);
        auditDto.setEntityJson(oldEntityJson);
        auditDto.setCreatedBy(oldAuditDto.getCreatedBy());
        auditDto.setCreatedAt(oldAuditDto.getCreatedAt());
        auditDto.setEntityId(account.getId());

        auditService.logAudit(auditDto);
    }

    @After("execution(* com.bank.account.service.AccountServiceImpl.deleteAccount(com.bank.account.entity.Account))")
    public void logDeleteAccount(JoinPoint joinPoint) {
        String oldEntityJson;

        Account account = (Account) joinPoint.getArgs()[0];
        String entityJson = JsonUtils.convertToJson(account);

        AuditDto oldAuditDto = auditService.getAuditByEntityId(account.getId());

        AuditDto auditDto = new AuditDto();
        auditDto.setEntityJson(entityJson);
        auditDto.setOperationType("deleteAccount");
        auditDto.setModifiedAt(new Timestamp(System.currentTimeMillis()));
        auditDto.setEntityType(ENTITY_TYPE);
        auditDto.setModifiedBy(CURRENT_USER);

        if (oldAuditDto.getNewEntityJson() == null) {
            oldEntityJson = oldAuditDto.getEntityJson();
        } else {
            oldEntityJson = oldAuditDto.getNewEntityJson();
        }

        auditDto.setEntityJson(oldEntityJson);
        auditDto.setCreatedBy(oldAuditDto.getCreatedBy());
        auditDto.setCreatedAt(oldAuditDto.getCreatedAt());
        auditDto.setEntityId(account.getId());

        auditService.logAudit(auditDto);
    }

}
