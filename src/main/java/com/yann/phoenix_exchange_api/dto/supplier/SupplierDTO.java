package com.yann.phoenix_exchange_api.dto.supplier;

import com.yann.phoenix_exchange_api.entity.purchase.SupplierType;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplierDTO {
    private Long id;
    private String name;
    private SupplierType type;
    private String email;
    private String phone;
    private String address;
    private BigDecimal rating;
    private BigDecimal totalPurchases;
    private String vatNumber;
    private Boolean isActive;
    private LocalDateTime createdAt;

    // Stats
    private Integer totalPurchaseOrders;
    private Integer totalProductsSupplied;
    private Boolean isReliable;
}
