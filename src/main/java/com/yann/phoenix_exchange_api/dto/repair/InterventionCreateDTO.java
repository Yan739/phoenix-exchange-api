package com.yann.phoenix_exchange_api.dto.repair;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InterventionCreateDTO {

    @NotNull(message = "Ticket ID is required")
    private Long ticketId;

    @NotNull(message = "Technician ID is required")
    private Long technicianId;

    @NotBlank(message = "Intervention type is required")
    @Size(max = 100)
    private String type;

    @NotBlank(message = "Description is required")
    @Size(max = 5000)
    private String description;

    @Size(max = 1000)
    private String partsUsed;

    @Min(value = 0, message = "Duration must be positive")
    private Integer durationMinutes;

    @DecimalMin(value = "0.0", message = "Cost must be positive")
    private BigDecimal cost;
}
