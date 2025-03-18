package com.bank.account.aspects;

import com.bank.account.dto.AuditDto;
import com.bank.account.producers.AuditProducer;
import com.bank.account.service.AuditService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;


/**
 * Аспект для аудита операций с банковскими счетами.
 * Логирует создание и обновление счетов, а также отправляет события аудита.
 */
@Aspect
@Component
@Slf4j
public class AuditAspect {
    private final AuditProducer auditProducer;

    /**
     * Конструктор для создания экземпляра AuditAspect.
     *
     * @see com.bank.account.service.AuditServiceImpl
     * @param auditProducer Продьюсер для отправки событий Аудита
     */
    public AuditAspect(AuditProducer auditProducer) {
        this.auditProducer = auditProducer;
    }

    /**
     * Логирует операцию создания нового счета.
     * Вызывается после успешного выполнения метода createNewAccount в сервисе AccountServiceImpl.
     *
     * @param joinPoint точка соединения, предоставляющая информацию о методе
     * @param result результат выполнения метода createNewAccount (созданный AccountDto)
     */
    @AfterReturning(pointcut = "execution(* com.bank.account.service.AccountServiceImpl.createNewAccount(..))",
            returning = "result")
    public void logCreateAccount(JoinPoint joinPoint, Object result) {
        try {
            auditProducer.sentAuditLogRequest(result, "create");

            log.info("Aspect 'create' completed successfully");
        } catch (Exception e) {
            log.error("Error in aspect 'create' operation: {}", e.getMessage());
        }
    }

    /**
     * Логирует операцию обновления существующего счета.
     * Вызывается после успешного выполнения метода updateCurrentAccount в сервисе AccountServiceImpl.
     * Метод извлекает идентификатор счета из аргументов метода, преобразует обновленные данные счета в JSON,
     * получает предыдущие данные счета из аудита, создает новую запись аудита с обновленными данными и отправляет событие аудита.
     *
     * @param joinPoint точка соединения, предоставляющая информацию о методе и его аргументах
     * @param result результат выполнения метода updateCurrentAccount (обновленный AccountDto)
     */
    @AfterReturning(pointcut = "execution(* com.bank.account.service.AccountServiceImpl.updateCurrentAccount(Long,..))",
            returning = "result")
    public void logUpdateCurrentAccount(JoinPoint joinPoint, Object result) {
        try {
            auditProducer.sentAuditLogRequest(result, "update");

            log.info("Aspect 'update' completed successfully");
        } catch (Exception e) {
            log.error("Failed to update audit: {}", e.getMessage());
        }
    }
}

