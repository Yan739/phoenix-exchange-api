package com.yann.phoenix_exchange_api.mapper;

import com.yann.phoenix_exchange_api.dto.warehouse.InventoryMovementCreateDTO;
import com.yann.phoenix_exchange_api.dto.warehouse.InventoryMovementDTO;
import com.yann.phoenix_exchange_api.entity.inventory.InventoryMovement;
import org.springframework.stereotype.Component;

@Component
public class InventoryMovementMapper {

    public InventoryMovementDTO toDTO(InventoryMovement entity) {
        if (entity == null) return null;

        return InventoryMovementDTO.builder()
                .id(entity.getId())
                .productId(entity.getProduct().getId())
                .productName(entity.getProduct().getName())
                .productSerialNumber(entity.getProduct().getSerialNumber())
                .productBrand(entity.getProduct().getBrand())
                .productCategory(entity.getProduct().getCategory())
                .warehouseId(entity.getWarehouse() != null ? entity.getWarehouse().getId() : null)
                .warehouseName(entity.getWarehouse() != null ? entity.getWarehouse().getName() : null)
                .movementType(entity.getMovementType())
                .quantity(entity.getQuantity())
                .referenceType(entity.getReferenceType())
                .referenceId(entity.getReferenceId())
                .referenceNumber(getReferenceNumber(entity))
                .notes(entity.getNotes())
                .createdById(entity.getCreatedBy() != null ? entity.getCreatedBy().getId() : null)
                .createdByName(entity.getCreatedBy() != null ? entity.getCreatedBy().getFullName() : null)
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public InventoryMovement toEntity(InventoryMovementCreateDTO dto) {
        if (dto == null) return null;

        return InventoryMovement.builder()
                .movementType(dto.getMovementType())
                .quantity(dto.getQuantity())
                .referenceType(dto.getReferenceType())
                .referenceId(dto.getReferenceId())
                .notes(dto.getNotes())
                .build();
    }

    private String getReferenceNumber(InventoryMovement entity) {
        if (entity.getReferenceId() == null) return null;
        return entity.getReferenceType() + "-" + entity.getReferenceId();
    }
}