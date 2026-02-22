package com.yann.phoenix_exchange_api.dto.repair;

import com.yann.phoenix_exchange_api.entity.product.Grade;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CloseTicketDTO {

    @NotNull(message = "Grade is required")
    private Grade grade;

    private String notes;
}
