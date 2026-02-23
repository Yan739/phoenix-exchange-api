package com.yann.phoenix_exchange_api.dto.dashboard;

import lombok.*;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KPIResponseDTO {
    private String label;
    private String description;
    private BigDecimal value;
    private String unit;
    private String formattedValue;

    // Comparison
    private BigDecimal previousValue;
    private BigDecimal change;
    private Double changePercentage;
    private String trend;

    // Visual indicators
    private String status;
    private String color;
    private String icon;

    // Target
    private BigDecimal targetValue;
    private Double progressToTarget;
    private Boolean targetReached;
}