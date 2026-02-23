package com.yann.phoenix_exchange_api.dto.purchase;

import com.yann.phoenix_exchange_api.entity.purchase.OrderStatus;
import jakarta.validation.constraints.DecimalMin;
import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderUpdateDTO {

    private OrderStatus status;

    @DecimalMin("0.0")
    private BigDecimal totalAmount;

    private String notes;
}