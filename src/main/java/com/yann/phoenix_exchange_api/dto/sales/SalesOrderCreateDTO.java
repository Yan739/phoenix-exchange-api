package com.yann.phoenix_exchange_api.dto.sales;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalesOrderCreateDTO {

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotEmpty(message = "Order must contain at least one item")
    private List<OrderItemCreateDTO> items;

    private String paymentMethod;

    @DecimalMin("0.0")
    private BigDecimal discountAmount;

    @Size(max = 1000)
    private String notes;
}
