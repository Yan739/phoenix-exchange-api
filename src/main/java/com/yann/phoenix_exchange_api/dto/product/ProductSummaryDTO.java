package com.yann.phoenix_exchange_api.dto.product;

import com.yann.phoenix_exchange_api.entity.product.ProductStatus;
import lombok.*;
import com.yann.phoenix_exchange_api.entity.product.Grade;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSummaryDTO {
    private Long id;
    private String serialNumber;
    private String name;
    private String brand;
    private BigDecimal sellPrice;
    private ProductStatus status;
    private Grade grade;
}

