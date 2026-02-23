package com.yann.phoenix_exchange_api.dto.purchase;

import com.yann.phoenix_exchange_api.entity.purchase.OrderStatus;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderSummaryDTO {
    private Long id;
    private String orderNumber;
    private String supplierName;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private LocalDateTime createdAt;
}
