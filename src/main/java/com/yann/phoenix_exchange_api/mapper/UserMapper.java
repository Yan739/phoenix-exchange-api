package com.yann.phoenix_exchange_api.mapper;

import com.yann.phoenix_exchange_api.dto.user.UserCreateDTO;
import com.yann.phoenix_exchange_api.dto.user.UserDTO;
import com.yann.phoenix_exchange_api.dto.user.UserSummaryDTO;
import com.yann.phoenix_exchange_api.dto.user.UserUpdateDTO;
import com.yann.phoenix_exchange_api.entity.user.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDTO toDTO(User entity) {
        if (entity == null) return null;

        return UserDTO.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .email(entity.getEmail())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .fullName(entity.getFullName())
                .role(entity.getRole())
                .isActive(entity.getIsActive())
                .phone(entity.getPhone())
                .createdAt(entity.getCreatedAt())
                .lastLogin(entity.getLastLogin())
                .build();
    }

    public User toEntity(UserCreateDTO dto) {
        if (dto == null) return null;

        return User.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .role(dto.getRole())
                .phone(dto.getPhone())
                .isActive(true)
                .build();
    }

    public UserSummaryDTO toSummaryDTO(User entity) {
        if (entity == null) return null;

        return UserSummaryDTO.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .fullName(entity.getFullName())
                .role(entity.getRole())
                .isActive(entity.getIsActive())
                .build();
    }

    public void updateEntityFromDTO(UserUpdateDTO dto, User entity) {
        if (dto == null || entity == null) return;

        if (dto.getEmail() != null) {
            entity.setEmail(dto.getEmail());
        }
        if (dto.getFirstName() != null) {
            entity.setFirstName(dto.getFirstName());
        }
        if (dto.getLastName() != null) {
            entity.setLastName(dto.getLastName());
        }
        if (dto.getRole() != null) {
            entity.setRole(dto.getRole());
        }
        if (dto.getIsActive() != null) {
            entity.setIsActive(dto.getIsActive());
        }
        if (dto.getPhone() != null) {
            entity.setPhone(dto.getPhone());
        }
    }
}