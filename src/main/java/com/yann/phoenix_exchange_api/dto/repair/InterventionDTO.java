package com.yann.phoenix_exchange_api.dto.repair;

import com.yann.phoenix_exchange_api.entity.repair.InterventionStatus;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterventionDTO {
    private Long id;
    private Long ticketId;
    private Long technicianId;
    private String technicianName;
    private String type;
    private String description;
    private String partsUsed;
    private Integer durationMinutes;
    private BigDecimal cost;
    private InterventionStatus status;
    private LocalDateTime createdAt;
}

