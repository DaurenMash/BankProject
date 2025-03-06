package com.bank.authorization;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import com.bank.authorization.entity.User;
import com.bank.authorization.repository.UserRepository;

@Component
public class TestDataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public TestDataInitializer(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Проверяем, существует ли уже тестовый пользователь
        if (userRepository.findByProfileId(1L).isEmpty()) {
            // Создаем тестового пользователя
            User adminUser = new User();
            adminUser.setProfileId(1L);
            adminUser.setRole("ROLE_ADMIN");
            adminUser.setPassword(passwordEncoder.encode("admin123"));

            // Сохраняем пользователя в базу данных
            userRepository.save(adminUser);
            System.out.println("Test admin user created.");
        }
    }
}