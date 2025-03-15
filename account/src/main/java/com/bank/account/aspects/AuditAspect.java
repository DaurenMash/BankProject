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

/**
 * Аспект для аудита операций с банковскими счетами.
 * Логирует создание и обновление счетов, а также отправляет события аудита.
 */
@Aspect
@Component
@Slf4j
public class AuditAspect {
    private final AuditService auditService; //сервис для работы с аудитом
    private final AuditProducer auditProducer; //Продьюсер для отправки событий аудита
    private final String ENTITY_TYPE = "Account"; //Тип сущности, к которой относится данная запись в аудите
    private final String CURRENT_USER = "SYSTEM"; //Пользователь, который выполнил действие с сущностью
    private final String CREATE_OPERATION = "CREATION"; //Операция создания аккаунта
    private final String UPDATE_OPERATION = "UPDATE"; //Операция изменения данных аккаунта

    /**
     * Конструктор для создания экземпляра AuditAspect.
     *
     * @param auditService сервис для работы с Аудитом
     * @see com.bank.account.service.AuditServiceImpl
     * @param auditProducer Продьюсер для отправки событий Аудита
     */
    public AuditAspect(AuditService auditService, AuditProducer auditProducer) {
        this.auditService = auditService;
        this.auditProducer = auditProducer;
    }

    /**
     * Логирует операцию создания нового счета.
     * Вызывается после _______________________________________________.
     *
     * @param joinPoint точка соединения, предоставляющая информацию о методе
     * @param result результат выполнения метода createNewAccount (созданный AccountDto)
     */
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

