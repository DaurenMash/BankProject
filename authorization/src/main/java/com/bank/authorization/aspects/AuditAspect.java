package com.bank.authorization.aspects;

import com.bank.authorization.dto.AuditDto;
import com.bank.authorization.dto.KafkaRequest;
import com.bank.authorization.dto.UserDto;
import com.bank.authorization.service.AuditService;
import com.bank.authorization.utils.JsonUtils;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Aspect
@RequiredArgsConstructor
public class AuditAspect {

    private static final String SYSTEM_USER = "SYSTEM";
    private static final String ENTITY_USER = "User";

    private final AuditService auditService;

    @Pointcut("execution(* com.bank.authorization.service.UserServiceImpl.save(..))")
    public void saveUserPointcut() {}

    @Pointcut("execution(* com.bank.authorization.service.UserServiceImpl.updateUser(..))")
    public void updateUserPointcut() {}

    @AfterReturning(pointcut = "saveUserPointcut() || updateUserPointcut()", returning = "result")
    public void logSaveOrUpdate(JoinPoint joinPoint, Object result) {

        Object[] args = joinPoint.getArgs();
        if (args.length > 0) {

            UserDto userDto = (UserDto) result;
            String methodName = joinPoint.getSignature().getName();

            final AuditDto auditDto = new AuditDto();
            auditDto.setEntityType("User");

            switch (methodName) {
                case "save":
                    auditDto.setOperationType("CREATE");
                    auditDto.setCreatedBy(SYSTEM_USER);
                    auditDto.setEntityJson(JsonUtils.toJson(userDto));
                    auditDto.setEntityType(ENTITY_USER);
                    auditDto.setModifiedBy("");
                    auditDto.setNewEntityJson(JsonUtils.toJson("{}"));
                    auditDto.setCreatedAt(LocalDateTime.now());
                    auditDto.setModifiedAt(LocalDateTime.now());

                    auditService.save(auditDto);
                    break;

                case "updateUser":

                    auditDto.setOperationType("UPDATE");
                    //auditDto.setCreatedBy(SYSTEM_USER);
                    //auditDto.setEntityJson(JsonUtils.toJson(userDto));
                    //auditDto.setEntityType(ENTITY_USER);
                    auditDto.setModifiedBy(SYSTEM_USER);
                    auditDto.setNewEntityJson(JsonUtils.toJson(userDto));
                    //auditDto.setCreatedAt(LocalDateTime.now());
                    auditDto.setModifiedAt(LocalDateTime.now());

                    auditService.updateAuditForUser(userDto.getId(), auditDto);

                    break;
            }
        }
    }
}
