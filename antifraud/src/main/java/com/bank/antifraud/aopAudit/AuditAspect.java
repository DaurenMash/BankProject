package com.bank.antifraud.aopAudit;

import com.bank.antifraud.dto.AuditDto;
import com.bank.antifraud.globalException.JsonProcessingException;
import com.bank.antifraud.kafkaProducer.AuditProducer;
import com.bank.antifraud.repository.SuspiciousAccountTransferRepository;
import com.bank.antifraud.repository.SuspiciousCardTransferRepository;
import com.bank.antifraud.service.AuditService;
import com.bank.antifraud.service.SuspiciousTransferServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Aspect
@Component
@Slf4j
public class AuditAspect {
    private final AuditProducer auditProducer;
    private final AuditService auditService;
    private final ObjectMapper objectMapper;
    private final SuspiciousTransferServiceImpl suspiciousTransferService;

    public AuditAspect(AuditProducer auditProducer, AuditService auditService, ObjectMapper objectMapper, SuspiciousTransferServiceImpl suspiciousTransferService) {
        this.auditProducer = auditProducer;
        this.auditService = auditService;
        this.objectMapper = objectMapper;
        this.suspiciousTransferService = suspiciousTransferService;
    }

    @AfterReturning(pointcut = "execution(* com.bank.antifraud.service.SuspiciousTransferServiceImpl.analyzeAccountTransfer(..)) || " +
            "execution(* com.bank.antifraud.service.SuspiciousTransferServiceImpl.analyzePhoneTransfer(..)) || " +
            "execution(* com.bank.antifraud.service.SuspiciousTransferServiceImpl.analyzeCardTransfer(..))",
            returning = "result")
    public void logCreateSuspicious(JoinPoint joinPoint, Object result) {
        try {
            String methodName = joinPoint.getSignature().getName();

            Object[] args = joinPoint.getArgs();
            Integer entityId = extractEntityId(args, methodName);

            auditService.createAudit(
                    "CREATE",
                    result.getClass().getSimpleName(),
                    "anti_fraud_system",
                    null,
                    result
            );

            log.info("Audit logged for {} operation: {}",  methodName);
        } catch (Exception e) {
            log.error("Failed to audit operation: {}", e.getMessage(), e);
        }
    }

    private Integer extractEntityId(Object[] args, String methodName) {
        if (methodName.startsWith("update")) {
            return (Integer) args[0];
        } else if (methodName.startsWith("create")) {
            return (Integer) args[1];
        }
        return null;
    }

    private Object getOldEntity(String methodName, Integer entityId) {
        try {
            if (methodName.contains("Card")) {
                return suspiciousTransferService.getCardTransfer(entityId);
            } else if (methodName.contains("Phone")) {
                return suspiciousTransferService.getPhoneTransfer(entityId);
            } else if (methodName.contains("Account")) {
                return suspiciousTransferService.getAccountTransfer(entityId);
            }
        } catch (Exception e) {
            log.error("Failed to get old entity: {}", e.getMessage());
        }
        return null;
    }
}
