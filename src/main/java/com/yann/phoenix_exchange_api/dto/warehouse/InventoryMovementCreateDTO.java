package com.yann.phoenix_exchange_api.dto.warehouse;

import com.yann.phoenix_exchange_api.entity.inventory.MovementType;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryMovementCreateDTO {

    @NotNull(message = "Product ID is required")
    private Long productId;

    private Long warehouseId;

    @NotNull(message = "Movement type is required")
    private MovementType movementType;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Max(value = 10000, message = "Quantity cannot exceed 10,000")
    private Integer quantity;

    @Size(max = 100)
    private String referenceType;

    private Long referenceId;

    @Size(max = 1000)
    private String notes;
}
