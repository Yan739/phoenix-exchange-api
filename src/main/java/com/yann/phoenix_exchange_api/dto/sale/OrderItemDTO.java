package com.yann.phoenix_exchange_api.dto.sale;

import lombok.*;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDTO {
    private Long id;
    private Long orderId;
    private Long productId;
    private String productName;
    private String productSerialNumber;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
}