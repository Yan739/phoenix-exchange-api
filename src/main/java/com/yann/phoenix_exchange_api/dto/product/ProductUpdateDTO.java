package com.yann.phoenix_exchange_api.dto.product;

import com.yann.phoenix_exchange_api.entity.product.Grade;
import com.yann.phoenix_exchange_api.entity.product.ProductStatus;
import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductUpdateDTO {
    private String name;
    private BigDecimal sellPrice;
    private ProductStatus status;
    private Grade grade;
    private Long warehouseId;
}
