package com.yann.phoenix_exchange_api.mapper;

import com.yann.phoenix_exchange_api.dto.supplier.SupplierCreateDTO;
import com.yann.phoenix_exchange_api.dto.supplier.SupplierDTO;
import com.yann.phoenix_exchange_api.dto.supplier.SupplierSummaryDTO;
import com.yann.phoenix_exchange_api.dto.supplier.SupplierUpdateDTO;
import com.yann.phoenix_exchange_api.entity.purchase.Supplier;
import org.springframework.stereotype.Component;

@Component
public class SupplierMapper {

    public SupplierDTO toDTO(Supplier entity) {
        if (entity == null) return null;

        return SupplierDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .type(entity.getType())
                .email(entity.getEmail())
                .phone(entity.getPhone())
                .address(entity.getAddress())
                .rating(entity.getRating())
                .totalPurchases(entity.getTotalPurchases())
                .vatNumber(entity.getVatNumber())
                .isActive(entity.getIsActive())
                .createdAt(entity.getCreatedAt())
                .totalPurchaseOrders(entity.getPurchaseOrders() != null ? entity.getPurchaseOrders().size() : 0)
                .totalProductsSupplied(entity.getProducts() != null ? entity.getProducts().size() : 0)
                .isReliable(entity.isReliable())
                .build();
    }

    public Supplier toEntity(SupplierCreateDTO dto) {
        if (dto == null) return null;

        return Supplier.builder()
                .name(dto.getName())
                .type(dto.getType())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .address(dto.getAddress())
                .rating(dto.getRating())
                .vatNumber(dto.getVatNumber())
                .isActive(true)
                .build();
    }

    public SupplierSummaryDTO toSummaryDTO(Supplier entity) {
        if (entity == null) return null;

        return SupplierSummaryDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .type(entity.getType())
                .rating(entity.getRating())
                .isActive(entity.getIsActive())
                .build();
    }

    public void updateEntityFromDTO(SupplierUpdateDTO dto, Supplier entity) {
        if (dto == null || entity == null) return;

        if (dto.getName() != null) entity.setName(dto.getName());
        if (dto.getType() != null) entity.setType(dto.getType());
        if (dto.getEmail() != null) entity.setEmail(dto.getEmail());
        if (dto.getPhone() != null) entity.setPhone(dto.getPhone());
        if (dto.getAddress() != null) entity.setAddress(dto.getAddress());
        if (dto.getRating() != null) entity.setRating(dto.getRating());
        if (dto.getVatNumber() != null) entity.setVatNumber(dto.getVatNumber());
        if (dto.getIsActive() != null) entity.setIsActive(dto.getIsActive());
    }
}