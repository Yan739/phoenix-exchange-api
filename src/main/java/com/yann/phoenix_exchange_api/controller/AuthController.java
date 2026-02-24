package com.yann.phoenix_exchange_api.controller;

import com.yann.phoenix_exchange_api.dto.auth.LoginRequestDTO;
import com.yann.phoenix_exchange_api.dto.auth.LoginResponseDTO;
import com.yann.phoenix_exchange_api.dto.auth.RegisterRequestDTO;
import com.yann.phoenix_exchange_api.dto.common.MessageResponseDTO;
import com.yann.phoenix_exchange_api.security.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        LoginResponseDTO response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<MessageResponseDTO> register(@Valid @RequestBody RegisterRequestDTO request) {
        authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                MessageResponseDTO.builder()
                        .message("User registered successfully")
                        .success(true)
                        .build()
        );
    }

    @PostMapping("/change-password")
    public ResponseEntity<MessageResponseDTO> changePassword(
            @RequestParam Long userId,
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {
        authService.changePassword(userId, oldPassword, newPassword);
        return ResponseEntity.ok(
                MessageResponseDTO.builder()
                        .message("Password changed successfully")
                        .success(true)
                        .build()
        );
    }
}