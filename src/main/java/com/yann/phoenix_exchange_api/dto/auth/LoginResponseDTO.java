package com.yann.phoenix_exchange_api.dto.auth;

import com.yann.phoenix_exchange_api.entity.user.UserRole;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {
    private String token;
    private String type = "Bearer";
    private Long userId;
    private String username;
    private String email;
    private UserRole role;
}
