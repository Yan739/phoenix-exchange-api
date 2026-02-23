package com.yann.phoenix_exchange_api.dto.warehouse;

import com.yann.phoenix_exchange_api.entity.inventory.WarehouseType;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseDTO {
    private Long id;
    private String name;
    private String location;
    private WarehouseType type;
    private Integer capacity;
    private Integer currentStock;
    private Boolean isActive;

    // Calculated fields
    private Double occupancyRate;
    private Integer availableSpace;
    private Boolean isFull;
    private Boolean isNearCapacity; // > 80%

    // Stats
    private Integer totalProducts;
    private Integer totalMovements;
}