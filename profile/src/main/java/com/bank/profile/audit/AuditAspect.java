package com.bank.profile.audit;

import com.bank.profile.service.AuditService;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AuditAspect {
    private final AuditService auditService;

    public AuditAspect(AuditService auditService) {
        this.auditService = auditService;
    }

    @Pointcut("execution(* com.bank.profile.service.*.create(..)) && !target(com.bank.profile.service.AuditService)")
    public void serviceCreate() {}

    @Pointcut("execution(* com.bank.profile.service.*.update(..)) && !target(com.bank.profile.service.AuditService)")
    public void serviceUpdate() {}

    @AfterReturning(pointcut = "serviceCreate()", returning = "value")
    public void logCreate(Object value) {
        auditService.create(value);
    }

    @AfterReturning(pointcut = "serviceUpdate()", returning = "value")
    public void logUpdate(Object value) {
        auditService.update(value);
    }
}