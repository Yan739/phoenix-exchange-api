package com.yann.phoenix_exchange_api.dto.product;

import com.yann.phoenix_exchange_api.entity.product.Grade;
import com.yann.phoenix_exchange_api.entity.product.ProductStatus;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private Long id;
    private String serialNumber;
    private String name;
    private String brand;
    private String category;
    private BigDecimal purchasePrice;
    private BigDecimal sellPrice;
    private ProductStatus status;
    private Grade grade;
    private Integer year;

    // Relations (IDs and names)
    private Long supplierId;
    private String supplierName;
    private Long purchaseOrderId;
    private String purchaseOrderNumber;
    private Long warehouseId;
    private String warehouseName;

    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // calculated fields
    private BigDecimal margin;
    private BigDecimal marginPercentage;
    private Boolean availableForSale;

    // Optional stats
    private Integer daysInStock;
    private Boolean hasActiveRepairTicket;
}