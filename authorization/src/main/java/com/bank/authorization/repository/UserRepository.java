package com.bank.authorization.repository;

import com.bank.authorization.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<Object> findByProfileId(Long profileIdLong);
}
