package com.bank.authorization.aspects;

import com.bank.authorization.dto.UserDto;
import com.bank.authorization.service.AuditService;
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
    public void saveUserPointcut() {

    }

    @Pointcut("execution(* com.bank.authorization.service.UserServiceImpl.updateUser(..))")
    public void updateUserPointcut() {

    }

    @AfterReturning(pointcut = "saveUserPointcut()", returning = "result")
    public void logSave(JoinPoint joinPoint, Object result) {
        if (result instanceof UserDto userDto) {
            auditService.logUserCreation(userDto);
        }
    }

    @AfterReturning(pointcut = "updateUserPointcut()", returning = "result")
    public void logUpdate(JoinPoint joinPoint, Object result) {
        if (result instanceof UserDto userDto) {
            auditService.logUserUpdate(userDto.getId(), userDto);
        }
    }
}

