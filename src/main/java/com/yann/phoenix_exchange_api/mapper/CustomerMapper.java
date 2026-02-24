package com.yann.phoenix_exchange_api.mapper;

import com.yann.phoenix_exchange_api.dto.customer.CustomerCreateDTO;
import com.yann.phoenix_exchange_api.dto.customer.CustomerDTO;
import com.yann.phoenix_exchange_api.dto.customer.CustomerSummaryDTO;
import com.yann.phoenix_exchange_api.dto.customer.CustomerUpdateDTO;
import com.yann.phoenix_exchange_api.entity.sale.Customer;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CustomerMapper {

    public CustomerDTO toDTO(Customer entity) {
        if (entity == null) return null;

        return CustomerDTO.builder()
                .id(entity.getId())
                .type(entity.getType())
                .name(entity.getName())
                .email(entity.getEmail())
                .phone(entity.getPhone())
                .address(entity.getAddress())
                .vatNumber(entity.getVatNumber())
                .loyaltyPoints(entity.getLoyaltyPoints())
                .totalSpent(entity.getTotalSpent())
                .segment(entity.getSegment())
                .isActive(entity.getIsActive())
                .createdAt(entity.getCreatedAt())
                .isVIP(entity.isVIP())
                .discountRate(entity.applyDiscount())
                .totalOrders(entity.getSalesOrders() != null ? entity.getSalesOrders().size() : 0)
                .lastOrderDate(getLastOrderDate(entity))
                .build();
    }

    public Customer toEntity(CustomerCreateDTO dto) {
        if (dto == null) return null;

        return Customer.builder()
                .type(dto.getType())
                .name(dto.getName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .address(dto.getAddress())
                .vatNumber(dto.getVatNumber())
                .isActive(true)
                .build();
    }

    public CustomerSummaryDTO toSummaryDTO(Customer entity) {
        if (entity == null) return null;

        return CustomerSummaryDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .type(entity.getType())
                .segment(entity.getSegment())
                .totalSpent(entity.getTotalSpent())
                .isActive(entity.getIsActive())
                .build();
    }

    public void updateEntityFromDTO(CustomerUpdateDTO dto, Customer entity) {
        if (dto == null || entity == null) return;

        if (dto.getType() != null) entity.setType(dto.getType());
        if (dto.getName() != null) entity.setName(dto.getName());
        if (dto.getEmail() != null) entity.setEmail(dto.getEmail());
        if (dto.getPhone() != null) entity.setPhone(dto.getPhone());
        if (dto.getAddress() != null) entity.setAddress(dto.getAddress());
        if (dto.getVatNumber() != null) entity.setVatNumber(dto.getVatNumber());
        if (dto.getIsActive() != null) entity.setIsActive(dto.getIsActive());
    }

    private LocalDateTime getLastOrderDate(Customer entity) {
        if (entity.getSalesOrders() == null || entity.getSalesOrders().isEmpty()) {
            return null;
        }
        return entity.getSalesOrders().stream()
                .map(order -> order.getCreatedAt())
                .max(LocalDateTime::compareTo)
                .orElse(null);
    }
}