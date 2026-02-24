package com.yann.phoenix_exchange_api.mapper;

import com.yann.phoenix_exchange_api.dto.purchase.PurchaseOrderCreateDTO;
import com.yann.phoenix_exchange_api.dto.purchase.PurchaseOrderDTO;
import com.yann.phoenix_exchange_api.dto.purchase.PurchaseOrderSummaryDTO;
import com.yann.phoenix_exchange_api.dto.purchase.PurchaseOrderUpdateDTO;
import com.yann.phoenix_exchange_api.entity.purchase.PurchaseOrder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Component
public class PurchaseOrderMapper {

    public PurchaseOrderDTO toDTO(PurchaseOrder entity) {
        if (entity == null) return null;

        return PurchaseOrderDTO.builder()
                .id(entity.getId())
                .orderNumber(entity.getOrderNumber())
                .supplierId(entity.getSupplier().getId())
                .supplierName(entity.getSupplier().getName())
                .totalAmount(entity.getTotalAmount())
                .status(entity.getStatus())
                .createdById(entity.getCreatedBy() != null ? entity.getCreatedBy().getId() : null)
                .createdByName(entity.getCreatedBy() != null ? entity.getCreatedBy().getFullName() : null)
                .approvedById(entity.getApprovedBy() != null ? entity.getApprovedBy().getId() : null)
                .approvedByName(entity.getApprovedBy() != null ? entity.getApprovedBy().getFullName() : null)
                .notes(entity.getNotes())
                .createdAt(entity.getCreatedAt())
                .receivedAt(entity.getReceivedAt())
                .productsCount(entity.getProducts() != null ? entity.getProducts().size() : 0)
                .requiresManagerApproval(entity.requiresManagerApproval())
                .daysSinceCreation(calculateDaysSinceCreation(entity))
                .build();
    }

    public PurchaseOrder toEntity(PurchaseOrderCreateDTO dto) {
        if (dto == null) return null;

        return PurchaseOrder.builder()
                .totalAmount(dto.getTotalAmount())
                .notes(dto.getNotes())
                .build();
    }

    public PurchaseOrderSummaryDTO toSummaryDTO(PurchaseOrder entity) {
        if (entity == null) return null;

        return PurchaseOrderSummaryDTO.builder()
                .id(entity.getId())
                .orderNumber(entity.getOrderNumber())
                .supplierName(entity.getSupplier().getName())
                .totalAmount(entity.getTotalAmount())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public void updateEntityFromDTO(PurchaseOrderUpdateDTO dto, PurchaseOrder entity) {
        if (dto == null || entity == null) return;

        if (dto.getStatus() != null) entity.setStatus(dto.getStatus());
        if (dto.getTotalAmount() != null) entity.setTotalAmount(dto.getTotalAmount());
        if (dto.getNotes() != null) entity.setNotes(dto.getNotes());
    }

    private Integer calculateDaysSinceCreation(PurchaseOrder entity) {
        if (entity.getCreatedAt() == null) return null;
        return (int) ChronoUnit.DAYS.between(entity.getCreatedAt(), LocalDateTime.now());
    }
}