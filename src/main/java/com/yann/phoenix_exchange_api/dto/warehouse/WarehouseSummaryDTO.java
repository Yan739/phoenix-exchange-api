package com.yann.phoenix_exchange_api.dto.warehouse;

import com.yann.phoenix_exchange_api.entity.inventory.WarehouseType;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseSummaryDTO {
    private Long id;
    private String name;
    private String location;
    private WarehouseType type;
    private Integer currentStock;
    private Integer capacity;
    private Double occupancyRate;
    private Boolean isActive;
}
