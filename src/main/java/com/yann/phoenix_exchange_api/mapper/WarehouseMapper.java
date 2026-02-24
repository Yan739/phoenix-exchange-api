package com.yann.phoenix_exchange_api.mapper;

import com.yann.phoenix_exchange_api.dto.warehouse.WarehouseCreateDTO;
import com.yann.phoenix_exchange_api.dto.warehouse.WarehouseDTO;
import com.yann.phoenix_exchange_api.dto.warehouse.WarehouseSummaryDTO;
import com.yann.phoenix_exchange_api.dto.warehouse.WarehouseUpdateDTO;
import com.yann.phoenix_exchange_api.entity.inventory.Warehouse;
import org.springframework.stereotype.Component;

@Component
public class WarehouseMapper {

    public WarehouseDTO toDTO(Warehouse entity) {
        if (entity == null) return null;

        return WarehouseDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .location(entity.getLocation())
                .type(entity.getType())
                .capacity(entity.getCapacity())
                .currentStock(entity.getCurrentStock())
                .isActive(entity.getIsActive())
                .occupancyRate(entity.getOccupancyRate())
                .availableSpace(calculateAvailableSpace(entity))
                .isFull(entity.isFull())
                .isNearCapacity(isNearCapacity(entity))
                .totalProducts(entity.getProducts() != null ? entity.getProducts().size() : 0)
                .totalMovements(entity.getInventoryMovements() != null ? entity.getInventoryMovements().size() : 0)
                .build();
    }

    public Warehouse toEntity(WarehouseCreateDTO dto) {
        if (dto == null) return null;

        return Warehouse.builder()
                .name(dto.getName())
                .location(dto.getLocation())
                .type(dto.getType())
                .capacity(dto.getCapacity())
                .currentStock(0)
                .isActive(true)
                .build();
    }

    public WarehouseSummaryDTO toSummaryDTO(Warehouse entity) {
        if (entity == null) return null;

        return WarehouseSummaryDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .location(entity.getLocation())
                .type(entity.getType())
                .currentStock(entity.getCurrentStock())
                .capacity(entity.getCapacity())
                .occupancyRate(entity.getOccupancyRate())
                .isActive(entity.getIsActive())
                .build();
    }

    public void updateEntityFromDTO(WarehouseUpdateDTO dto, Warehouse entity) {
        if (dto == null || entity == null) return;

        if (dto.getName() != null) entity.setName(dto.getName());
        if (dto.getLocation() != null) entity.setLocation(dto.getLocation());
        if (dto.getType() != null) entity.setType(dto.getType());
        if (dto.getCapacity() != null) entity.setCapacity(dto.getCapacity());
        if (dto.getIsActive() != null) entity.setIsActive(dto.getIsActive());
    }

    private Integer calculateAvailableSpace(Warehouse entity) {
        if (entity.getCapacity() == null) return null;
        return entity.getCapacity() - entity.getCurrentStock();
    }

    private Boolean isNearCapacity(Warehouse entity) {
        if (entity.getCapacity() == null || entity.getCapacity() == 0) return false;
        return entity.getOccupancyRate() > 80.0;
    }
}