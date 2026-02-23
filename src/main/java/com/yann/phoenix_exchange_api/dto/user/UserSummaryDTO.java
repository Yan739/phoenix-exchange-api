package com.yann.phoenix_exchange_api.dto.user;

import com.yann.phoenix_exchange_api.entity.user.UserRole;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSummaryDTO {
    private Long id;
    private String username;
    private String fullName;
    private UserRole role;
    private Boolean isActive;
}
