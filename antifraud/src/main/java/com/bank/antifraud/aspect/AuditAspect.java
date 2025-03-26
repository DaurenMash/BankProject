package com.bank.antifraud.aspect;

import com.bank.antifraud.dto.AuditDto;
import com.bank.antifraud.kafkaProducer.AuditProducer;
import com.bank.antifraud.service.AuditService;
import com.bank.antifraud.service.SuspiciousTransferServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class AuditAspect {
    private final AuditProducer auditProducer;
    private final AuditService auditService;

    @AfterReturning(pointcut = "execution(* com.bank.antifraud.service.SuspiciousTransferServiceImpl.analyzeAccountTransfer(..)) || " +
            "execution(* com.bank.antifraud.service.SuspiciousTransferServiceImpl.analyzePhoneTransfer(..)) || " +
            "execution(* com.bank.antifraud.service.SuspiciousTransferServiceImpl.analyzeCardTransfer(..))",
            returning = "result")
    public void logAuditCreateSuspicious(JoinPoint joinPoint, Object result) {
        try {
            String methodName = joinPoint.getSignature().getName();
            AuditDto auditDto =auditService.createAudit(
                    "CREATE",
                    result.getClass().getSimpleName(),
                    "anti_fraud_system",
                    null,
                    result
            );
            auditProducer.sendAuditLog(auditDto);

            log.info("Audit logged for operation: {}",  methodName);
        } catch (Exception e) {
            log.error("Failed to audit operation: ", e);
        }
    }
}
