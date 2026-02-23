package com.yann.phoenix_exchange_api.dto.sales;

import com.yann.phoenix_exchange_api.entity.sale.PaymentStatus;
import com.yann.phoenix_exchange_api.entity.sale.SalesOrderStatus;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalesOrderDTO {
    private Long id;
    private String orderNumber;

    // Customer
    private Long customerId;
    private String customerName;

    // Amounts
    private BigDecimal totalAmount;
    private BigDecimal taxAmount;
    private BigDecimal discountAmount;
    private BigDecimal grandTotal;

    // Status
    private SalesOrderStatus status;
    private String paymentMethod;
    private PaymentStatus paymentStatus;

    // Items
    private List<OrderItemDTO> orderItems;
    private Integer itemsCount;

    // Payments
    private List<PaymentDTO> payments;
    private Integer paymentsCount;

    // Notes
    private String notes;

    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime deliveredAt;

    // Calculated
    private Integer daysSinceCreation;
    private Boolean canBeCancelled;
}