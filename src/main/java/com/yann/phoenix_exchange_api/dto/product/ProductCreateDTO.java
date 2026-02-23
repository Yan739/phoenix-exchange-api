package com.yann.phoenix_exchange_api.dto.product;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreateDTO {

    @NotBlank(message = "Serial number is required")
    @Size(max = 255)
    private String serialNumber;

    @NotBlank(message = "Product name is required")
    @Size(max = 255)
    private String name;

    @Size(max = 100)
    private String brand;

    @Size(max = 100)
    private String category;

    @DecimalMin(value = "0.0", message = "Purchase price must be positive")
    private BigDecimal purchasePrice;

    @Min(value = 1900, message = "Year must be after 1900")
    @Max(value = 2100, message = "Year must be before 2100")
    private Integer year;

    private Long supplierId;
    private Long purchaseOrderId;
    private Long warehouseId;
}


