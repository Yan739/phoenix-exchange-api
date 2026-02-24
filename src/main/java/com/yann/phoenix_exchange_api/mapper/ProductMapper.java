package com.yann.phoenix_exchange_api.mapper;

import com.yann.phoenix_exchange_api.dto.product.ProductCreateDTO;
import com.yann.phoenix_exchange_api.dto.product.ProductDTO;
import com.yann.phoenix_exchange_api.dto.product.ProductSummaryDTO;
import com.yann.phoenix_exchange_api.dto.product.ProductUpdateDTO;
import com.yann.phoenix_exchange_api.entity.product.Product;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Component
public class ProductMapper {

    public ProductDTO toDTO(Product entity) {
        if (entity == null) return null;

        return ProductDTO.builder()
                .id(entity.getId())
                .serialNumber(entity.getSerialNumber())
                .name(entity.getName())
                .brand(entity.getBrand())
                .category(entity.getCategory())
                .purchasePrice(entity.getPurchasePrice())
                .sellPrice(entity.getSellPrice())
                .status(entity.getStatus())
                .grade(entity.getGrade())
                .year(entity.getYear())
                .supplierId(entity.getSupplier() != null ? entity.getSupplier().getId() : null)
                .supplierName(entity.getSupplier() != null ? entity.getSupplier().getName() : null)
                .purchaseOrderId(entity.getPurchaseOrder() != null ? entity.getPurchaseOrder().getId() : null)
                .purchaseOrderNumber(entity.getPurchaseOrder() != null ? entity.getPurchaseOrder().getOrderNumber() : null)
                .warehouseId(entity.getWarehouse() != null ? entity.getWarehouse().getId() : null)
                .warehouseName(entity.getWarehouse() != null ? entity.getWarehouse().getName() : null)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .margin(entity.calculateMargin())
                .marginPercentage(entity.calculateMarginPercentage())
                .availableForSale(entity.isAvailableForSale())
                .daysInStock(calculateDaysInStock(entity))
                .hasActiveRepairTicket(hasActiveRepairTicket(entity))
                .build();
    }

    public Product toEntity(ProductCreateDTO dto) {
        if (dto == null) return null;

        return Product.builder()
                .serialNumber(dto.getSerialNumber())
                .name(dto.getName())
                .brand(dto.getBrand())
                .category(dto.getCategory())
                .purchasePrice(dto.getPurchasePrice())
                .year(dto.getYear())
                .build();
    }

    public ProductSummaryDTO toSummaryDTO(Product entity) {
        if (entity == null) return null;

        return ProductSummaryDTO.builder()
                .id(entity.getId())
                .serialNumber(entity.getSerialNumber())
                .name(entity.getName())
                .brand(entity.getBrand())
                .category(entity.getCategory())
                .sellPrice(entity.getSellPrice())
                .status(entity.getStatus())
                .grade(entity.getGrade())
                .build();
    }

    public void updateEntityFromDTO(ProductUpdateDTO dto, Product entity) {
        if (dto == null || entity == null) return;

        if (dto.getName() != null) {
            entity.setName(dto.getName());
        }
        if (dto.getSellPrice() != null) {
            entity.setSellPrice(dto.getSellPrice());
        }
        if (dto.getStatus() != null) {
            entity.changeStatus(dto.getStatus());
        }
        if (dto.getGrade() != null) {
            entity.assignGrade(dto.getGrade());
        }
    }

    private Integer calculateDaysInStock(Product entity) {
        if (entity.getCreatedAt() == null) return null;
        return (int) ChronoUnit.DAYS.between(entity.getCreatedAt(), LocalDateTime.now());
    }

    private Boolean hasActiveRepairTicket(Product entity) {
        if (entity.getRepairTickets() == null) return false;
        return entity.getRepairTickets().stream()
                .anyMatch(ticket -> ticket.getClosedAt() == null);
    }
}
