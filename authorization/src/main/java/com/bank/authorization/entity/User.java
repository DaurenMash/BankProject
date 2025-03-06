package com.bank.authorization.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class User {

    private static final int ROLE_LENGTH = 40; // Длина поля role
    private static final int PASSWORD_LENGTH = 500; // Длина поля password

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "role", length = ROLE_LENGTH, nullable = false)
    private String role;

    @Column(name = "profile_id", nullable = false)
    private Long profileId;

    @Column(name = "password", length = PASSWORD_LENGTH, nullable = false)
    private String password;
}
