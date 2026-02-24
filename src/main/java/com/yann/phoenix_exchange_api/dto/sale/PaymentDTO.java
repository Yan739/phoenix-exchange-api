package com.yann.phoenix_exchange_api.dto.sale;

import com.yann.phoenix_exchange_api.entity.sale.PaymentMethod;
import com.yann.phoenix_exchange_api.entity.sale.PaymentStatus;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO {
    private Long id;
    private Long orderId;
    private String orderNumber;
    private BigDecimal amount;
    private PaymentMethod method;
    private PaymentStatus status;
    private String transactionId;
    private String reference;
    private LocalDateTime paidAt;
    private LocalDateTime createdAt;
}