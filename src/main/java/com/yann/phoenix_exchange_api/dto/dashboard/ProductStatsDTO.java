package com.yann.phoenix_exchange_api.dto.dashboard;

import com.yann.phoenix_exchange_api.entity.product.Grade;
import com.yann.phoenix_exchange_api.entity.product.ProductStatus;
import lombok.*;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductStatsDTO {
    private Integer totalProducts;

    // By Status
    private Map<ProductStatus, Integer> productsByStatus;

    // By Grade
    private Map<Grade, Integer> productsByGrade;

    // By Category
    private Map<String, Integer> productsByCategory;

    // By Brand
    private Map<String, Integer> topBrands;

    // Turnover
    private Double averageDaysInStock;
    private Integer productsTurnedOverThisMonth;
    private Double inventoryTurnoverRate;
}