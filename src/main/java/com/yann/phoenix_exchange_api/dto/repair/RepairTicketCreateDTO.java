package com.yann.phoenix_exchange_api.dto.repair;

import com.yann.phoenix_exchange_api.entity.repair.Priority;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RepairTicketCreateDTO {

    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotNull
    private Priority priority;

    private BigDecimal estimatedCost;
    private Integer estimatedDurationHours;
    private String diagnosis;
}
