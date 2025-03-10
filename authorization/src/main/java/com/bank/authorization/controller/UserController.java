package com.bank.authorization.controller;

import com.bank.authorization.dto.AuditDto;
import com.bank.authorization.dto.UserDto;
import com.bank.authorization.service.AuditService;
import com.bank.authorization.service.UserService;
import com.bank.authorization.utils.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j // Добавлено для логирования
public class UserController {

    private static final String SYSTEM_USER = "SYSTEM";
    private static final String ENTITY_USER = "User";

    private final UserService userService;
    private final AuditService auditService;

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.info("Fetching all users");
        try {
            List<UserDto> users = userService.getAllUsers();
            log.info("Successfully fetched {} users", users.size());
            return users;
        } catch (Exception e) {
            log.error("Failed to fetch users: {}", e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable("id") Long id) {
        log.info("Fetching user by id: {}", id);
        try {
            UserDto user = userService.getUserById(id);
            log.info("Successfully fetched user with id: {}", id);
            return user;
        } catch (Exception e) {
            log.error("Failed to fetch user with id {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@RequestBody UserDto userDto) {
        log.info("Creating user with profileId: {}", userDto.getProfileId());
        try {
            final UserDto savedUserDto = userService.save(userDto);

            final AuditDto auditDto = new AuditDto();
            auditDto.setEntityType(ENTITY_USER);
            auditDto.setOperationType("CREATE");
            auditDto.setModifiedBy(SYSTEM_USER);
            auditDto.setEntityJson(JsonUtils.toJson(savedUserDto));
            auditDto.setCreatedBy(SYSTEM_USER);
            auditDto.setNewEntityJson(JsonUtils.toJson("{}"));
            auditDto.setCreatedAt(LocalDateTime.now());
            auditDto.setModifiedAt(LocalDateTime.now());

            auditService.save(auditDto);

            log.info("User created successfully with id: {}", savedUserDto.getId());
            return savedUserDto;
        } catch (Exception e) {
            log.error("Failed to create user: {}", e.getMessage(), e);
            throw e;
        }
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto updateUser(@PathVariable("id") Long id, @RequestBody UserDto userDto) {
        log.info("Updating user with id: {}", id);
        try {
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

            log.info("User updated successfully with id: {}", id);
            return updatedUserDto;
        } catch (Exception e) {
            log.error("Failed to update user with id {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable("id") Long id) {
        log.info("Deleting user with id: {}", id);
        try {
            userService.deleteById(id);
            log.info("User deleted successfully with id: {}", id);
        } catch (Exception e) {
            log.error("Failed to delete user with id {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }
}
