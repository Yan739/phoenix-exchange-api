package com.yann.phoenix_exchange_api.dto.supplier;

import com.yann.phoenix_exchange_api.entity.purchase.SupplierType;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplierCreateDTO {

    @NotBlank(message = "Supplier name is required")
    @Size(max = 255)
    private String name;

    @NotNull(message = "Supplier type is required")
    private SupplierType type;

    @Email(message = "Email must be valid")
    private String email;

    @Pattern(regexp = "^[+]?[(]?[0-9]{1,4}[)]?[-\\s\\.]?[(]?[0-9]{1,4}[)]?[-\\s\\.]?[0-9]{1,9}$")
    private String phone;

    @Size(max = 500)
    private String address;

    @DecimalMin("0.0")
    @DecimalMax("5.0")
    private BigDecimal rating;

    @Size(max = 100)
    private String vatNumber;
}
