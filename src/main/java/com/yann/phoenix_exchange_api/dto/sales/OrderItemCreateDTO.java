package com.yann.phoenix_exchange_api.dto.sales;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemCreateDTO {

    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotNull
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    @NotNull
    @DecimalMin(value = "0.0", message = "Unit price must be positive")
    private BigDecimal unitPrice;
}

