package com.bank.authorization.securirty;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import com.bank.authorization.security.CustomUserDetailsService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.bank.authorization.entity.User;
import com.bank.authorization.repository.UserRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private UserRepository userRepository;

    @Test
    void shouldLoadUserByUsernameWhenExists() {
        User expectedUser = new User();
        expectedUser.setProfileId(12345L);
        expectedUser.setPassword("password");
        expectedUser.setRole("USER_ROLE");

        when(userRepository.findByProfileId(any())).thenReturn(java.util.Optional.of(expectedUser));

        var loadedUser = customUserDetailsService.loadUserByUsername(Long.toString(expectedUser.getProfileId()));

        List<GrantedAuthority> actualAuthorities = new ArrayList<>(loadedUser.getAuthorities());

        verify(userRepository).findByProfileId(expectedUser.getProfileId());
        assertEquals(expectedUser.getProfileId().toString(), loadedUser.getUsername(), "Идентификатор профиля должен соответствовать.");
        assertEquals(expectedUser.getPassword(), loadedUser.getPassword(), "Пароль должен соответствовать.");
        assertEquals(Collections.singletonList(new SimpleGrantedAuthority(expectedUser.getRole())),
                actualAuthorities,
                "Роли должны соответствовать.");
    }

    @Test
    void shouldThrowExceptionWhenUserDoesNotExist() {
        String validProfileIdAsString = "123";

        when(userRepository.findByProfileId(Long.valueOf(validProfileIdAsString)))
                .thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            customUserDetailsService.loadUserByUsername(validProfileIdAsString);
        });
    }

    @AfterEach
    void resetMocks() {
        Mockito.reset(userRepository); 
    }
}