package com.bank.authorization.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.bank.authorization.entity.User;
import com.bank.authorization.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomUserDetailsService.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String profileId) throws UsernameNotFoundException {
        LOGGER.debug("Loading user by profileId: " + profileId);
        // Преобразуем profileId из String в Long
        final Long profileIdLong = Long.parseLong(profileId);

        // Ищем пользователя по profile_id
        final User user = (User) userRepository.findByProfileId(profileIdLong)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with profileId: " + profileId));

        // Создаем список authorities на основе роли пользователя
        final List<GrantedAuthority> authorities = Collections
                .singletonList(new SimpleGrantedAuthority(user.getRole()));

        // Возвращаем UserDetails
        return new org.springframework.security.core.userdetails.User(
                user.getProfileId().toString(), // Используем profileId как username
                user.getPassword(),
                true, // Аккаунт всегда активен
                true, // Пароль не истек
                true, // Аккаунт не заблокирован
                true, // Учетные данные не истекли
                authorities);
    }
}
