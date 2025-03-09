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

    public AuditAspect(AuditService auditService, AccountService accountService) {
        this.auditService = auditService;
        this.accountService = accountService;
    }

    @After("execution(* com.bank.account.service.AccountServiceImpl.createNewAccount(..))")
    public void logCreateAccount(JoinPoint joinPoint) {
        System.out.println("логирование сохранения вызывается");
        Account account = (Account) joinPoint.getArgs()[0];
        String entityJson = JsonUtils.convertToJson(account);
        AuditDto auditDto = new AuditDto();

        auditDto.setEntityJson(entityJson);
        auditDto.setOperationType("createNewAccount");
        auditDto.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        auditDto.setEntityType("Account");
        auditDto.setCreatedBy("SYSTEM");

        auditService.logAudit(auditDto);
    }

    @After("execution(* com.bank.account.service.AccountServiceImpl.updateCurrentAccount())")
    public void logUpdateCurrentAccount(JoinPoint joinPoint) {
        System.out.println("логирование изменения вызывается");
        Long accountId = (Long) joinPoint.getArgs()[0];
        Account account = (Account) joinPoint.getArgs()[1];
        String entityJson = JsonUtils.convertToJson(account);
        String oldEntityJson = JsonUtils.convertToJson(accountService.getAccountById(accountId));
        AuditDto auditDto = new AuditDto();

        auditDto.setEntityJson(oldEntityJson);
        auditDto.setNewEntityJson(entityJson);
        auditDto.setOperationType("updateCurrentAccount");
        auditDto.setModifiedAt(new Timestamp(System.currentTimeMillis()));
        auditDto.setEntityType("Account");
        auditDto.setModifiedBy("SYSTEM");

        auditService.logAudit(auditDto);
    }

    @After("execution(* com.bank.account.service.AccountServiceImpl.deleteAccount())")
    public void logDeleteAccount(JoinPoint joinPoint) {
        System.out.println("логирование удалеения вызывается");
        Account account = (Account) joinPoint.getArgs()[0];
        String entityJson = JsonUtils.convertToJson(account);
        AuditDto auditDto = new AuditDto();

        auditDto.setEntityJson(entityJson);
        auditDto.setOperationType("deleteAccount");
        auditDto.setModifiedAt(new Timestamp(System.currentTimeMillis()));
        auditDto.setEntityType("Account");
        auditDto.setModifiedBy("SYSTEM");

        auditService.logAudit(auditDto);
    }

}
