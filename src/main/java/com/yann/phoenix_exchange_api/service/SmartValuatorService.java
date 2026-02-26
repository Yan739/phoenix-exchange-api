package com.yann.phoenix_exchange_api.service;

import com.yann.phoenix_exchange_api.dto.valuator.EstimationRequestDTO;
import com.yann.phoenix_exchange_api.dto.valuator.EstimationResponseDTO;
import com.yann.phoenix_exchange_api.entity.pricing.SmartValuatorEstimation;
import com.yann.phoenix_exchange_api.mapper.SmartValuatorEstimationMapper;
import com.yann.phoenix_exchange_api.repository.PricingRuleRepository;
import com.yann.phoenix_exchange_api.repository.SmartValuatorEstimationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SmartValuatorService {

    private final SmartValuatorEstimationRepository estimationRepository;
    private final PricingRuleRepository pricingRuleRepository;
    private final SmartValuatorEstimationMapper estimationMapper;

    @Value("https://router.huggingface.co/v1/chat/completions")
    private String huggingFaceApiUrl;

    @Value("${HF_API_KEY}")
    private String hfApiKey;

    public EstimationResponseDTO estimate(EstimationRequestDTO request) {
        try {
            // TODO: Attempt AI estimation with Hugging Face
            return estimateWithAI(request);
        } catch (Exception e) {
            log.warn("AI estimation failed, using fallback pricing: {}", e.getMessage());
            return estimateWithFallback(request);
        }
    }

    private EstimationResponseDTO estimateWithAI(EstimationRequestDTO request) {
        // TODO: Implement real Hugging Face API call
        // For now, simulate a response

        BigDecimal basePrice = getBasePrice(request.getItemName(), request.getCategory());
        double conditionFactor = request.getConditionRating() / 10.0;
        BigDecimal estimatedPrice = basePrice.multiply(BigDecimal.valueOf(conditionFactor))
                .setScale(2, RoundingMode.HALF_UP);

        SmartValuatorEstimation estimation = estimationMapper.toEntity(request);
        estimation.setEstimatedPrice(estimatedPrice);
        estimation.setAiDescription("Estimation basée sur Llama 3.3-70B (simulated)");
        estimation.setConfidence(85);

        SmartValuatorEstimation saved = estimationRepository.save(estimation);

        return estimationMapper.toDTO(saved, false);
    }

    private EstimationResponseDTO estimateWithFallback(EstimationRequestDTO request) {
        BigDecimal basePrice = getBasePrice(request.getItemName(), request.getCategory());
        double conditionFactor = request.getConditionRating() / 10.0;
        BigDecimal estimatedPrice = basePrice.multiply(BigDecimal.valueOf(conditionFactor))
                .setScale(2, RoundingMode.HALF_UP);

        SmartValuatorEstimation estimation = estimationMapper.toEntity(request);
        estimation.setEstimatedPrice(estimatedPrice);
        estimation.setAiDescription("Estimation basée sur prix de référence (mode fallback)");
        estimation.setConfidence(60);

        SmartValuatorEstimation saved = estimationRepository.save(estimation);

        return estimationMapper.toDTO(saved, true);
    }

    private BigDecimal getBasePrice(String itemName, String category) {
        // Base price lookup table
        Map<String, BigDecimal> basePrices = Map.of(
                "iPhone 14", new BigDecimal("500"),
                "iPhone 13", new BigDecimal("380"),
                "iPhone 12", new BigDecimal("300"),
                "MacBook Pro", new BigDecimal("850"),
                "MacBook Air", new BigDecimal("550"),
                "Galaxy S23", new BigDecimal("470"),
                "Galaxy S24", new BigDecimal("520"),
                "iPad Pro", new BigDecimal("600"),
                "iPad Air", new BigDecimal("400")
        );

        return basePrices.getOrDefault(itemName, new BigDecimal("300"));
    }
}