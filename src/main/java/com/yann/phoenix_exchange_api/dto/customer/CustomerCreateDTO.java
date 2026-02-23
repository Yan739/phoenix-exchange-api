package com.yann.phoenix_exchange_api.dto.customer;

import com.yann.phoenix_exchange_api.entity.sale.CustomerType;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerCreateDTO {

    @NotNull(message = "Customer type is required")
    private CustomerType type;

    @NotBlank(message = "Customer name is required")
    @Size(max = 255)
    private String name;

    @Email(message = "Email must be valid")
    private String email;

    private String phone;

    @Size(max = 500)
    private String address;

    @Size(max = 100)
    private String vatNumber;
}
