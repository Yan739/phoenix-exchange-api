package com.yann.phoenix_exchange_api.dto.sale;

import com.yann.phoenix_exchange_api.entity.sale.SalesOrderStatus;
import jakarta.validation.constraints.DecimalMin;
import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalesOrderUpdateDTO {

    private SalesOrderStatus status;

    private String paymentMethod;

    @DecimalMin("0.0")
    private BigDecimal discountAmount;

    private String notes;
}
