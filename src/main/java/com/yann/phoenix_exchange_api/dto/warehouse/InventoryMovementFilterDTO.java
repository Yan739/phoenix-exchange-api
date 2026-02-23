package com.yann.phoenix_exchange_api.dto.warehouse;

import com.yann.phoenix_exchange_api.entity.inventory.MovementType;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryMovementFilterDTO {
    private Long productId;
    private Long warehouseId;
    private MovementType movementType;
    private String referenceType;
    private Long createdById;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}