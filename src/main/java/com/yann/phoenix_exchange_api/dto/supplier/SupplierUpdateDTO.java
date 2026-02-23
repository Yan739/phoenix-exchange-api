package com.yann.phoenix_exchange_api.dto.supplier;

import com.yann.phoenix_exchange_api.entity.purchase.SupplierType;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplierUpdateDTO {

    @Size(max = 255)
    private String name;

    private SupplierType type;

    @Email
    private String email;

    private String phone;

    @Size(max = 500)
    private String address;

    @DecimalMin("0.0")
    @DecimalMax("5.0")
    private BigDecimal rating;

    private String vatNumber;

    private Boolean isActive;
}
