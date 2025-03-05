package com.bank.authorization.aspects;

import com.bank.authorization.dto.AuditDto;
import com.bank.authorization.entity.User;
import com.bank.authorization.service.AuditService;
import com.bank.authorization.utils.JsonUtils;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Component
@Aspect
@RequiredArgsConstructor
public class AuditAspect {

    private final AuditService auditService;

    @Pointcut("execution(* com.bank.authorization.service.UserServiceImpl.save(..))")
    public void saveUserPointcut() {}

    @AfterReturning(pointcut = "saveUserPointcut()", returning = "result")
    public void logSave(JoinPoint joinPoint, User result) {
        AuditDto auditDto = new AuditDto();
        auditDto.setEntityType("User");

        if (result.getId() == null || result.getId() <= 0) { // Новый пользователь
            auditDto.setOperationType("CREATE");
            auditDto.setCreatedBy("SYSTEM");
            auditDto.setNewEntityJson(JsonUtils.toJson(result));
        } else { // Обновленный пользователь
            auditDto.setOperationType("UPDATE");
            auditDto.setModifiedBy("SYSTEM");
            auditDto.setEntityJson(JsonUtils.toJson(result));
        }

        auditService.save(auditDto);
    }
}