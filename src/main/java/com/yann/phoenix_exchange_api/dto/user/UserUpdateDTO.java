package com.yann.phoenix_exchange_api.dto.user;

import com.yann.phoenix_exchange_api.entity.user.UserRole;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDTO {

    @Email
    private String email;

    @Size(max = 100)
    private String firstName;

    @Size(max = 100)
    private String lastName;

    private UserRole role;

    private Boolean isActive;

    private String phone;
}

