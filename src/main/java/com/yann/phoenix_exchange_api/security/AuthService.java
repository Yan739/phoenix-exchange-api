package com.yann.phoenix_exchange_api.security;

import com.yann.phoenix_exchange_api.dto.auth.LoginRequestDTO;
import com.yann.phoenix_exchange_api.dto.auth.LoginResponseDTO;
import com.yann.phoenix_exchange_api.dto.auth.RegisterRequestDTO;
import com.yann.phoenix_exchange_api.entity.user.User;
import com.yann.phoenix_exchange_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;

    /**
     * Login user and return JWT token
     */
    public LoginResponseDTO login(LoginRequestDTO request) {
        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        // Generate JWT token
        String token = tokenProvider.generateToken(authentication);


        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));


        user.updateLastLogin();
        userRepository.save(user);

        log.info("User logged in: {}", user.getUsername());

        // Calculate expiration (8 hours from now)
        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(8 * 60 * 60);

        return LoginResponseDTO.builder()
                .token(token)
                .type("Bearer")
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())

                .role(user.getRole())
                .expiresAt(expiresAt)
                .build();
    }

    /**
     * Register new user
     */
    public void register(RegisterRequestDTO request) {
        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        // Create new user
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .role(request.getRole())
                .phone(request.getPhone())
                .isActive(true)
                .build();

        userRepository.save(user);

        log.info("New user registered: {}", user.getUsername());
    }

    /**
     * Change password
     */
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Verify old password
        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid old password");
        }

        // Update password
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        log.info("Password changed for user: {}", user.getUsername());
    }

    /**
     * Reset password (admin only)
     */
    public void resetPassword(Long userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        log.info("Password reset for user: {}", user.getUsername());
    }
}