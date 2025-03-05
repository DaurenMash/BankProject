package com.bank.authorization.service;

import com.bank.authorization.dto.UserDto;
import com.bank.authorization.entity.User;
import java.util.List;
import java.util.Optional;

public interface UserService {
    List<UserDto> getAllUsers();
    Optional<UserDto> getUserById(Long id);
    UserDto save(UserDto userDto);
    void deleteById(Long id);
}