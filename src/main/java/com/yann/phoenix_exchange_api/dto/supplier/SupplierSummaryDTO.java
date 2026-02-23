package com.yann.phoenix_exchange_api.dto.supplier;

import com.yann.phoenix_exchange_api.entity.purchase.SupplierType;
import lombok.*;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplierSummaryDTO {
    private Long id;
    private String name;
    private SupplierType type;
    private BigDecimal rating;
    private Boolean isActive;
}