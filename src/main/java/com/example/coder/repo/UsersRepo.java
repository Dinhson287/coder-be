package com.example.coder.repo;

import com.example.coder.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersRepo extends JpaRepository<Users, Long> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Optional<Users> findByUsername(String username);
    Optional<Users> findByEmail(String email);
}
