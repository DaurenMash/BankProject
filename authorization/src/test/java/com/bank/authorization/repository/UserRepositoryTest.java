package com.bank.authorization.repository;

import com.bank.authorization.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRepositoryTest {

    @Mock
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setProfileId(123L);
        user.setRole("ROLE_USER");
        user.setPassword("password");
    }

    @Test
    void testFindByProfileId() {
        when(userRepository.findByProfileId(123L)).thenReturn(Optional.of(user));

        Optional<User> foundUser = userRepository.findByProfileId(123L);

        assertTrue(foundUser.isPresent());
        assertEquals(123L, foundUser.get().getProfileId());

        verify(userRepository, times(1)).findByProfileId(123L);
    }
}
