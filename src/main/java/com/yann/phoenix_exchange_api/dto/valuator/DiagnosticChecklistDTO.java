package com.yann.phoenix_exchange_api.dto.valuator;

import com.yann.phoenix_exchange_api.entity.pricing.RiskLevel;
import lombok.*;
import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiagnosticChecklistDTO {
    private Long id;
    private Long estimationId;
    private Long productId;
    private Long technicianId;
    private String technicianName;

    private Map<String, Boolean> blockers;
    private Map<String, Object> hardwareTests;

    private BigDecimal deductionsTotal;
    private BigDecimal adjustedPrice;
    private RiskLevel riskLevel;

    private Boolean hasBlockers;
    private Boolean isProfitable;
}
