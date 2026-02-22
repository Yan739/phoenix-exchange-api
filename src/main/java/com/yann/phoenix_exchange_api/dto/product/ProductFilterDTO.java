package com.yann.phoenix_exchange_api.dto.product;

import com.yann.phoenix_exchange_api.entity.product.Grade;
import com.yann.phoenix_exchange_api.entity.product.ProductStatus;
import lombok.*;

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
}
