package com.yann.phoenix_exchange_api.dto.product;

import com.yann.phoenix_exchange_api.entity.product.Grade;
import com.yann.phoenix_exchange_api.entity.product.ProductStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductFilterDTO {
    private String category;
    private String brand;
    private ProductStatus status;
    private Grade grade;
    private Long supplierId;
    private Long warehouseId;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private LocalDateTime createdAfter;
    private LocalDateTime createdBefore;
}
