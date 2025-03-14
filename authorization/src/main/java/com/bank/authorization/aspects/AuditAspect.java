package com.bank.authorization.aspects;

import com.bank.authorization.dto.AuditDto;
import com.bank.authorization.dto.KafkaRequest;
import com.bank.authorization.dto.KafkaResponse;
import com.bank.authorization.dto.UserDto;
import com.bank.authorization.service.AuditService;
import com.bank.authorization.utils.JsonUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    @Pointcut("execution(* com.bank.authorization.service.UserCommandHandler.*(..))")
    public void saveUserPointcut() {
    }

    @AfterReturning(pointcut = "saveUserPointcut()", returning = "response")
    public void logSave(JoinPoint joinPoint, KafkaResponse response) {
        // Получаем аргументы метода
        Object[] args = joinPoint.getArgs();
        if (args.length > 0 && args[0] instanceof KafkaRequest) {
            KafkaRequest request = (KafkaRequest) args[0];
            Object payload = request.getPayload();

            // Конвертируем payload в UserDto
            ObjectMapper objectMapper = new ObjectMapper();
            UserDto userDto = objectMapper.convertValue(request.getPayload(), UserDto.class);
            userDto.setPassword("");

            final AuditDto auditDto = new AuditDto();
            auditDto.setEntityType("User");

            // Определяем тип операции на основе имени метода
            String methodName = joinPoint.getSignature().getName();
            switch (methodName) {
                case "handleCreateUser":
                    if (response.isSuccess()) {
                        auditDto.setOperationType("CREATE");
                        auditDto.setCreatedBy(SYSTEM_USER);
                        auditDto.setEntityJson(JsonUtils.toJson(userDto));
                        auditDto.setEntityType(ENTITY_USER);
                        auditDto.setModifiedBy("");
                        auditDto.setNewEntityJson(JsonUtils.toJson("{}"));
                        auditDto.setCreatedAt(LocalDateTime.now());
                        auditDto.setModifiedAt(LocalDateTime.now());

                    } else {
                        auditDto.setOperationType("CREATE_FAILED");
                        auditDto.setCreatedBy(SYSTEM_USER);
                        auditDto.setEntityJson(JsonUtils.toJson(userDto));
                        auditDto.setEntityType(ENTITY_USER);
                        auditDto.setModifiedBy("");
                        auditDto.setNewEntityJson(JsonUtils.toJson("{}"));
                        auditDto.setCreatedAt(LocalDateTime.now());
                        auditDto.setModifiedAt(LocalDateTime.now());
                    }
                    auditService.save(auditDto);
                    break;

                case "handleUpdateUser":
                    if (response.isSuccess()) {
                        auditDto.setOperationType("UPDATE");
                        //auditDto.setCreatedBy(SYSTEM_USER);
                        //auditDto.setEntityJson(JsonUtils.toJson(userDto));
                        //auditDto.setEntityType(ENTITY_USER);
                        auditDto.setModifiedBy(SYSTEM_USER);
                        auditDto.setNewEntityJson(JsonUtils.toJson(userDto));
                        //auditDto.setCreatedAt(LocalDateTime.now());
                        auditDto.setModifiedAt(LocalDateTime.now());
                        auditService.updateAuditForUser(userDto.getId(), auditDto);
                    } else {
                        auditDto.setOperationType("UPDATE_FAILED");
                        //auditDto.setCreatedBy(SYSTEM_USER);
                        //auditDto.setEntityJson(JsonUtils.toJson(userDto));
                        //auditDto.setEntityType(ENTITY_USER);
                        auditDto.setModifiedBy(SYSTEM_USER);
                        auditDto.setNewEntityJson(JsonUtils.toJson(userDto));
                        //auditDto.setCreatedAt(LocalDateTime.now());
                        auditDto.setModifiedAt(LocalDateTime.now());
                        auditService.save(auditDto);
                    }
                    break;
            }
        }
    }
}
