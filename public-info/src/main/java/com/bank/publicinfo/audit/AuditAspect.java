package com.bank.publicinfo.audit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;


/**
 * Аспект для аудита создания и обновления банков, отделений,
 * банкоматов, лицензий и сертификатов.
 */
@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class AuditAspect {

    private final AuditService auditService;

    /**
     * Логирует операции создания банка, отделения, лицензии, сертификата
     * @param result результат выполнения Target метода создания сущности в БД
     */

    @AfterReturning(pointcut = "execution(* com.bank.publicinfo.service.*Impl.create*(..))",
            returning = "result")
    public void afterCreateAdvice(Object result) {
        log.info("AfterCreateAdvice triggered with: {}", result.toString());
        try {
            auditService.createAudit(result);
            log.info("Audit created Successfully: {}", result);
        }
        catch (Exception e) {
            log.error("AfterCreateAdvice failed: {}", e.getMessage());
        }
    }


    /**
     * Логирует операции обновления банка, отделения, лицензии, сертификата
     * @param result результат выполнения Target метода обновления сущности в БД
     */
    @AfterReturning(pointcut = "execution(* com.bank.publicinfo.service.*Impl.update*(..))",
            returning = "result")
    public void afterUpdateAdvice(Object result) {
        log.info("AfterUpdateAdvice triggered with: {}", result.toString());
        try {
            auditService.updateAudit(result);
            log.info("Audit created successfully: {}", result);
        } catch (Exception e) {
            log.error("AfterUpdateAdvice failed: {}", e.getMessage());
        }
    }


}
