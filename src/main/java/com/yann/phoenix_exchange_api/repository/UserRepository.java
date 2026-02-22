package com.yann.phoenix_exchange_api.repository;

import com.yann.phoenix_exchange_api.entity.user.User;
import com.yann.phoenix_exchange_api.entity.user.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    List<User> findByRole(UserRole role);
    List<User> findByIsActive(Boolean isActive);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
}
