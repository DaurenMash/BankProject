package com.bank.authorization.controller;

import com.bank.authorization.dto.AuditDto;
import com.bank.authorization.dto.UserDto;
import com.bank.authorization.service.AuditService;
import com.bank.authorization.service.UserService;
import com.bank.authorization.utils.JsonUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuditService auditService;

    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public Optional<UserDto> getUserById(@PathVariable("id") Long id) {
        return userService.getUserById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@RequestBody UserDto userDto) {

        UserDto savedUserDto = userService.save(userDto);

        AuditDto auditDto = new AuditDto();
        auditDto.setEntityType("User");
        auditDto.setOperationType("CREATE");
        auditDto.setCreatedBy("SYSTEM");
        auditDto.setNewEntityJson(JsonUtils.toJson(savedUserDto));
        auditService.save(auditDto);

        return savedUserDto;
    }

    @PutMapping("/{id}")
    public UserDto updateUser(@PathVariable("id") Long id, @RequestBody UserDto userDto) {
        if (!id.equals(userDto.getId())) {
            throw new IllegalArgumentException("ID in path must match ID in request body");
        }

        UserDto updatedUserDto = userService.save(userDto);

        AuditDto auditDto = new AuditDto();
        auditDto.setEntityType("User");
        auditDto.setOperationType("UPDATE");
        auditDto.setModifiedBy("SYSTEM");
        auditDto.setEntityJson(JsonUtils.toJson(updatedUserDto));
        auditService.save(auditDto);

        return updatedUserDto;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable("id") Long id) {
        userService.deleteById(id);
    }
}