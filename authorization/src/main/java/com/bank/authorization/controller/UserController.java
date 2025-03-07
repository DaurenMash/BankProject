package com.bank.authorization.controller;

import com.bank.authorization.dto.AuditDto;
import com.bank.authorization.dto.UserDto;
import com.bank.authorization.service.AuditService;
import com.bank.authorization.service.UserService;
import com.bank.authorization.utils.JsonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Api(value = "REST API для User", description = "Выполняет REST API операции для User")
public class UserController {

    private static final String SYSTEM_USER = "SYSTEM";

    private static final String ENTITY_USER = "User";

    private final UserService userService;
    private final AuditService auditService;

    @GetMapping
    @ApiOperation(value = "Возвращает список всех пользователей", response = List.class)
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Возвращает пользователя по его ID", response = UserDto.class)
    public Optional<UserDto> getUserById(@PathVariable("id") Long id) {
        return userService.getUserById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "Создает нового пользователя", response = UserDto.class)
    public UserDto createUser(@RequestBody UserDto userDto) {

        final UserDto savedUserDto = userService.save(userDto);

        final AuditDto auditDto = new AuditDto();
        auditDto.setEntityType(ENTITY_USER);
        auditDto.setOperationType("CREATE");
        auditDto.setModifiedBy(SYSTEM_USER);
        auditDto.setEntityJson(JsonUtils.toJson(savedUserDto));
        auditDto.setCreatedBy(SYSTEM_USER);
        auditDto.setNewEntityJson(JsonUtils.toJson("{}")); // В описании поля сказано "заполняется при изменении"
        auditDto.setCreatedAt(LocalDateTime.now());
        auditDto.setModifiedAt(LocalDateTime.now());

        auditService.save(auditDto);

        return savedUserDto;
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Обновляет информацию о пользователе по его ID", response = UserDto.class)
    public UserDto updateUser(@PathVariable("id") Long id, @RequestBody UserDto userDto) {
        if (!id.equals(userDto.getId())) {
            throw new IllegalArgumentException("ID in path must match ID in request body");
        }

        final UserDto updatedUserDto = userService.updateUser(id, userDto);

        final AuditDto auditDto = new AuditDto();
        auditDto.setEntityType(ENTITY_USER);
        auditDto.setOperationType("UPDATE");
        auditDto.setModifiedBy(SYSTEM_USER);
        auditDto.setEntityJson(JsonUtils.toJson(updatedUserDto));
        auditDto.setCreatedBy(SYSTEM_USER);
        auditDto.setNewEntityJson(JsonUtils.toJson(updatedUserDto));
        auditDto.setCreatedAt(LocalDateTime.now());
        auditDto.setModifiedAt(LocalDateTime.now());

        auditService.save(auditDto);

        return updatedUserDto;
    }
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiOperation(value = "Удаляет пользователя по его ID")
    public void deleteUser(@PathVariable("id") Long id) {
        userService.deleteById(id);
    }
}
