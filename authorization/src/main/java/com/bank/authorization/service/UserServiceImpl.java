package com.bank.authorization.service;

import com.bank.authorization.dto.UserDto;
import com.bank.authorization.entity.User;
import com.bank.authorization.mapper.UserMapper;
import com.bank.authorization.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final String USER_NOT_FOUND_MESSAGE = "User not found with id: ";

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        log.info("Fetching all users from the database");
        final List<UserDto> users = userRepository.findAll().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
        log.info("Successfully fetched {} users", users.size());
        return users;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserById(Long id) {
        log.info("Fetching user by id: {}", id);
        final UserDto user = userRepository.findById(id)
                .map(userMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND_MESSAGE + id));
        log.info("Successfully fetched user with id: {}", id);
        return user;
    }

    @Override
    @Transactional
    public UserDto save(UserDto userDto) {
        log.info("Saving user with profileId: {}", userDto.getProfileId());

        encodePasswordIfPresent(userDto);

        final User user = userMapper.toEntity(userDto);
        final User savedUser = userRepository.save(user);
        log.info("User saved successfully with id: {}", savedUser.getId());
        return userMapper.toDto(savedUser);
    }

    @Override
    @Transactional
    public UserDto updateUser(Long id, UserDto userDto) {
        log.info("Updating user with id: {}", id);
        final User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND_MESSAGE + id));

        encodePasswordIfPresent(userDto);

        userMapper.updateEntityFromDto(userDto, existingUser);
        final User updatedUser = userRepository.save(existingUser);
        log.info("User updated successfully with id: {}", id);
        return userMapper.toDto(updatedUser);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        log.info("Deleting user with id: {}", id);
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException(USER_NOT_FOUND_MESSAGE + id);
        }
        userRepository.deleteById(id);
        log.info("User deleted successfully with id: {}", id);
    }

    private void encodePasswordIfPresent(UserDto userDto) {
        if (StringUtils.hasText(userDto.getPassword())) {
            userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }
    }
}
