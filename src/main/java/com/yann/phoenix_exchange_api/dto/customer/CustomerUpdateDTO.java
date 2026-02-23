package com.yann.phoenix_exchange_api.dto.customer;

import com.yann.phoenix_exchange_api.entity.sale.CustomerType;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerUpdateDTO {

    private CustomerType type;

    @Size(max = 255)
    private String name;

    @Email
    private String email;

    private String phone;

    @Size(max = 500)
    private String address;

    private String vatNumber;

    private Boolean isActive;
}
