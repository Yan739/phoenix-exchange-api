package com.yann.phoenix_exchange_api.dto.warehouse;

import com.yann.phoenix_exchange_api.entity.inventory.WarehouseType;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseUpdateDTO {

    @Size(min = 3, max = 255)
    private String name;

    @Size(max = 255)
    private String location;

    private WarehouseType type;

    @Min(value = 1)
    @Max(value = 100000)
    private Integer capacity;

    private Boolean isActive;
}