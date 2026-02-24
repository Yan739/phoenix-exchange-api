package com.yann.phoenix_exchange_api.mapper;

import com.yann.phoenix_exchange_api.dto.valuator.DiagnosticChecklistCreateDTO;
import com.yann.phoenix_exchange_api.dto.valuator.DiagnosticChecklistDTO;
import com.yann.phoenix_exchange_api.entity.pricing.DiagnosticChecklist;
import com.yann.phoenix_exchange_api.entity.pricing.RiskLevel;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DiagnosticChecklistMapper {

    public DiagnosticChecklistDTO toDTO(DiagnosticChecklist entity) {
        if (entity == null) return null;

        return DiagnosticChecklistDTO.builder()
                .id(entity.getId())
                .estimationId(entity.getEstimation() != null ? entity.getEstimation().getId() : null)
                .productId(entity.getProduct() != null ? entity.getProduct().getId() : null)
                .technicianId(entity.getTechnician() != null ? entity.getTechnician().getId() : null)
                .technicianName(entity.getTechnician() != null ? entity.getTechnician().getFullName() : null)
                .blockers(entity.getBlockers())
                .hardwareTests(entity.getHardwareTests())
                .deductionsTotal(entity.getDeductionsTotal())
                .adjustedPrice(entity.getAdjustedPrice())
                .riskLevel(entity.getRiskLevel())
                .hasBlockers(entity.hasBlockers())
                .isProfitable(determineIfProfitable(entity))
                .build();
    }

    public DiagnosticChecklist toEntity(DiagnosticChecklistCreateDTO dto) {
        if (dto == null) return null;

        return DiagnosticChecklist.builder()
                .blockers(dto.getBlockers())
                .hardwareTests(dto.getHardwareTests())
                .build();
    }

    private Boolean determineIfProfitable(DiagnosticChecklist entity) {
        if (entity.hasBlockers()) return false;
        if (entity.getEstimation() == null || entity.getDeductionsTotal() == null) return null;

        BigDecimal halfEstimatedPrice = entity.getEstimation().getEstimatedPrice()
                .multiply(new BigDecimal("0.5"));
        return entity.getDeductionsTotal().compareTo(halfEstimatedPrice) < 0;
    }

    private String generateRecommendation(DiagnosticChecklist entity) {
        if (entity.hasBlockers()) return "REJECT";
        if (entity.getRiskLevel() == RiskLevel.HIGH) return "NEGOTIATE";
        if (entity.getRiskLevel() == RiskLevel.MODERATE) return "NEGOTIATE";
        return "APPROVE";
    }
}