package com.yann.phoenix_exchange_api.dto.purchase;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderCreateDTO {

    @NotNull(message = "Supplier ID is required")
    private Long supplierId;

    @NotNull
    @DecimalMin(value = "0.01", message = "Total amount must be greater than 0")
    private BigDecimal totalAmount;

    @Size(max = 1000)
    private String notes;
}
