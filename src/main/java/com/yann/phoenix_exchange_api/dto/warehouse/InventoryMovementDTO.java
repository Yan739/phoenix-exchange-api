package com.yann.phoenix_exchange_api.dto.warehouse;

import com.yann.phoenix_exchange_api.entity.inventory.MovementType;
import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryMovementDTO {
    private Long id;

    // Product
    private Long productId;
    private String productName;
    private String productSerialNumber;
    private String productBrand;
    private String productCategory;

    // Warehouse
    private Long warehouseId;
    private String warehouseName;

    // Movement details
    private MovementType movementType;
    private Integer quantity;

    // Reference (order, ticket, etc.)
    private String referenceType;
    private Long referenceId;
    private String referenceNumber;

    // Notes
    private String notes;

    // User who created the movement
    private Long createdById;
    private String createdByName;

    // Timestamp
    private LocalDateTime createdAt;
}