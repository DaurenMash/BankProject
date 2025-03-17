package com.bank.authorization.service;

import com.bank.authorization.dto.UserDto;
import com.bank.authorization.entity.User;
import com.bank.authorization.mapper.UserMapper;
import com.bank.authorization.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final String USER_NOT_FOUND_MESSAGE = "User not found with id: ";

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        log.info("Fetching all users from the database");
        try {
            final List<UserDto> users = userRepository.findAll().stream()
                    .map(userMapper::toDto)
                    .collect(Collectors.toList());
            log.info("Successfully fetched {} users", users.size());
            return users;
        } catch (Exception e) {
            log.error("Failed to fetch users: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserById(Long id) {
        log.info("Fetching user by id: {}", id);
        try {
            final UserDto user = userRepository.findById(id)
                    .map(userMapper::toDto)
                    .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND_MESSAGE + id));
            log.info("Successfully fetched user with id: {}", id);
            return user;
        } catch (Exception e) {
            log.error("Failed to fetch user with id {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional
    public UserDto save(UserDto userDto) {
        log.info("Saving user with profileId: {}", userDto.getProfileId());
        try {
            final String encodedPassword = passwordEncoder.encode(userDto.getPassword());
            userDto.setPassword(encodedPassword);

            final User user = userMapper.toEntity(userDto);
            final User savedUser = userRepository.save(user);
            log.info("User saved successfully with id: {}", savedUser.getId());
            return userMapper.toDto(savedUser);
        } catch (Exception e) {
            log.error("Failed to save user: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional
    public UserDto updateUser(Long id, UserDto userDto) {
        log.info("Updating user with id: {}", id);
        try {
            final User existingUser = userRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND_MESSAGE + id));

            // Если пароль изменён, шифруем его
            if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
                final String encodedPassword = passwordEncoder.encode(userDto.getPassword());
                userDto.setPassword(encodedPassword);
            }

            userMapper.updateEntityFromDto(userDto, existingUser);
            final User updatedUser = userRepository.save(existingUser);
            log.info("User updated successfully with id: {}", id);
            return userMapper.toDto(updatedUser);
        } catch (Exception e) {
            log.error("Failed to update user with id {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        log.info("Deleting user with id: {}", id);
        try {
            if (!userRepository.existsById(id)) {
                throw new EntityNotFoundException(USER_NOT_FOUND_MESSAGE + id);
            }
            userRepository.deleteById(id);
            log.info("User deleted successfully with id: {}", id);
        } catch (Exception e) {
            log.error("Failed to delete user with id {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }
}
