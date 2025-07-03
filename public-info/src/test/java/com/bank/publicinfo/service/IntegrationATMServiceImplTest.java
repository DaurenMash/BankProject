package com.bank.publicinfo.service;

import com.bank.publicinfo.dto.ATMDto;
import com.bank.publicinfo.entity.ATM;
import com.bank.publicinfo.entity.Branch;
import com.bank.publicinfo.repository.AtmRepository;
import com.bank.publicinfo.repository.BranchRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Testcontainers
@SpringBootTest
@ActiveProfiles("local")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
public class IntegrationATMServiceImplTest {

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("postgresTestDB")
            .withUsername("postgres")
            .withPassword("password");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.liquibase.default-schema", () -> "public_info");
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @BeforeAll
    static void setupDatabase() throws SQLException {
        try (Connection conn = DriverManager.getConnection(
                postgreSQLContainer.getJdbcUrl(),
                postgreSQLContainer.getUsername(),
                postgreSQLContainer.getPassword())) {
            Statement stmt = conn.createStatement();
            stmt.execute("CREATE SCHEMA IF NOT EXISTS public_info");
            stmt.close();
        }
    }

    @Autowired
    private AtmRepository atmRepository;
    @Autowired
    private BranchRepository branchRepository;

    private ATM testAtm;
    private ATMDto testAtmDto;
    private Branch testBranch;

    @BeforeEach
    void cleanDatabase() {
        atmRepository.deleteAllInBatch();
        branchRepository.deleteAllInBatch();
    }

    private void createTestData() {
        // Создаем и сохраняем Branch
        testBranch = branchRepository.save(
                Branch.builder()
                        .address("Branch Address")
                        .phoneNumber(1234567890L)
                        .city("Test City")
                        .startOfWork(LocalTime.of(8, 0))
                        .endOfWork(LocalTime.of(20, 0))
                        .build()
        );

        // Создаем ATM с двусторонней связью
        testAtm = ATM.builder()
                .address("ATM Address")
                .startOfWork(LocalTime.of(8, 0))
                .endOfWork(LocalTime.of(20, 0))
                .allHours(false)
                .branch(testBranch)
                .build();

        // Устанавливаем связь с Branch
        testBranch.setAtms(new HashSet<>(Set.of(testAtm)));
        testAtm = atmRepository.save(testAtm);

        // Создаем DTO
        testAtmDto = ATMDto.builder()
                .id(testAtm.getId())
                .address(testAtm.getAddress())
                .startOfWork(testAtm.getStartOfWork())
                .endOfWork(testAtm.getEndOfWork())
                .allHours(testAtm.getAllHours())
                .branchId(testAtm.getBranch().getId())
                .build();
    }

    @Test
    void testEmptyGetAllAtms() {
        assertThat(atmRepository.findAll()).isEmpty();
    }

    @Test
    void testCreateAtm() {
        createTestData();

        ATM retrievedAtm = atmRepository.findById(testAtm.getId()).orElseThrow();

        assertThat(retrievedAtm)
                .usingRecursiveComparison()
                .ignoringFields("branch.atms") // Игнорируем циклическую зависимость
                .isEqualTo(testAtm);

        assertThat(retrievedAtm.getBranch().getId())
                .isEqualTo(testBranch.getId());
    }

    @Test
    void testCreateNewATM_RollbackOnException() {
        createTestData();
        assertThrows(RuntimeException.class, () -> atmRepository.save(null));
        assertThat(atmRepository.count()).isEqualTo(1); // Проверяем, что ATM не создался
    }
}
