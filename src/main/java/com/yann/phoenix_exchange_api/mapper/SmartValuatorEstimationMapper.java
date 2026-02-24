package com.yann.phoenix_exchange_api.mapper;

import com.yann.phoenix_exchange_api.dto.valuator.EstimationRequestDTO;
import com.yann.phoenix_exchange_api.dto.valuator.EstimationResponseDTO;
import com.yann.phoenix_exchange_api.entity.pricing.SmartValuatorEstimation;
import org.springframework.stereotype.Component;

@Component
public class SmartValuatorEstimationMapper {

    public EstimationResponseDTO toDTO(SmartValuatorEstimation entity, Boolean isFallback) {
        if (entity == null) return null;

        return EstimationResponseDTO.builder()
                .id(entity.getId())
                .estimatedPrice(entity.getEstimatedPrice())
                .aiDescription(entity.getAiDescription())
                .confidence(entity.getConfidence())
                .suggestedGrade(entity.suggestGrade())
                .isFallback(isFallback)
                .createdAt(entity.getCreatedAt())
                .itemName(entity.getItemName())
                .brand(entity.getBrand())
                .category(entity.getCategory())
                .conditionRating(entity.getConditionRating())
                .build();
    }

    public SmartValuatorEstimation toEntity(EstimationRequestDTO dto) {
        if (dto == null) return null;

        return SmartValuatorEstimation.builder()
                .itemName(dto.getItemName())
                .brand(dto.getBrand())
                .category(dto.getCategory())
                .year(dto.getYear())
                .conditionRating(dto.getConditionRating())
                .build();
    }
}