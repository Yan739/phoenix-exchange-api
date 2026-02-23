package com.yann.phoenix_exchange_api.dto.purchase;

import com.yann.phoenix_exchange_api.entity.purchase.OrderStatus;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderDTO {
    private Long id;
    private String orderNumber;

    // Supplier
    private Long supplierId;
    private String supplierName;

    // Amount
    private BigDecimal totalAmount;

    // Status
    private OrderStatus status;

    // Users
    private Long createdById;
    private String createdByName;
    private Long approvedById;
    private String approvedByName;

    // Notes
    private String notes;

    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime receivedAt;

    // Products
    private Integer productsCount;

    // Calculated
    private Boolean requiresManagerApproval;
    private Integer daysSinceCreation;
}
