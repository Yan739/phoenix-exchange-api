package com.yann.phoenix_exchange_api.dto.repair;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InterventionCreateDTO {

    @NotNull
    private Long ticketId;

    @NotNull
    private Long technicianId;

    @NotBlank
    private String type;

    @NotBlank
    private String description;

    private String partsUsed;

    @Min(0)
    private Integer durationMinutes;

    @DecimalMin("0.0")
    private BigDecimal cost;
}
