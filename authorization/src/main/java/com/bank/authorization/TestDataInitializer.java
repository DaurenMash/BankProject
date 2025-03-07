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

        if (userRepository.findByProfileId(1L).isEmpty()) {

            User adminUser = new User();
            adminUser.setProfileId(1L);
            adminUser.setRole("ROLE_ADMIN");
            adminUser.setPassword(passwordEncoder.encode("admin123"));

            userRepository.save(adminUser);
            System.out.println("Test ADMIN user created.");
        }

        if (userRepository.findByProfileId(2L).isEmpty()) {

            User adminUser = new User();
            adminUser.setProfileId(2L);
            adminUser.setRole("ROLE_USER");
            adminUser.setPassword(passwordEncoder.encode("user123"));

            userRepository.save(adminUser);
            System.out.println("Test USER user created.");
        }

    }
}