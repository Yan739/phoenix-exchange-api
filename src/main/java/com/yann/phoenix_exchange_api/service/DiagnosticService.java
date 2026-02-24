package com.yann.phoenix_exchange_api.service;

import com.yann.phoenix_exchange_api.dto.valuator.DiagnosticChecklistCreateDTO;
import com.yann.phoenix_exchange_api.dto.valuator.DiagnosticChecklistDTO;
import com.yann.phoenix_exchange_api.dto.valuator.PurchasePriceCalculationDTO;
import com.yann.phoenix_exchange_api.entity.pricing.DiagnosticChecklist;
import com.yann.phoenix_exchange_api.entity.pricing.SmartValuatorEstimation;
import com.yann.phoenix_exchange_api.entity.product.Product;
import com.yann.phoenix_exchange_api.entity.user.User;
import com.yann.phoenix_exchange_api.exception.ResourceNotFoundException;
import com.yann.phoenix_exchange_api.mapper.DiagnosticChecklistMapper;
import com.yann.phoenix_exchange_api.repository.DiagnosticChecklistRepository;
import com.yann.phoenix_exchange_api.repository.ProductRepository;
import com.yann.phoenix_exchange_api.repository.SmartValuatorEstimationRepository;
import com.yann.phoenix_exchange_api.repository.UserRepository;
import com.yann.phoenix_exchange_api.utils.PriceCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DiagnosticService {

    private final DiagnosticChecklistRepository diagnosticChecklistRepository;
    private final SmartValuatorEstimationRepository estimationRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final DiagnosticChecklistMapper diagnosticChecklistMapper;

    public DiagnosticChecklistDTO getChecklistById(Long id) {
        DiagnosticChecklist checklist = diagnosticChecklistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Diagnostic checklist not found: " + id));
        return diagnosticChecklistMapper.toDTO(checklist);
    }

    public DiagnosticChecklistDTO createChecklist(DiagnosticChecklistCreateDTO createDTO) {
        SmartValuatorEstimation estimation = estimationRepository.findById(createDTO.getEstimationId())
                .orElseThrow(() -> new ResourceNotFoundException("Estimation not found"));

        User technician = userRepository.findById(createDTO.getTechnicianId())
                .orElseThrow(() -> new ResourceNotFoundException("Technician not found"));

        Product product = null;
        if (createDTO.getProductId() != null) {
            product = productRepository.findById(createDTO.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        }

        DiagnosticChecklist checklist = diagnosticChecklistMapper.toEntity(createDTO);
        checklist.setEstimation(estimation);
        checklist.setProduct(product);
        checklist.setTechnician(technician);

        // Calculate deductions from hardware tests
        BigDecimal deductions = calculateDeductions(createDTO.getHardwareTests());
        checklist.setDeductionsTotal(deductions);

        // Calculate adjusted price
        BigDecimal adjustedPrice = estimation.getEstimatedPrice().subtract(deductions);
        checklist.setAdjustedPrice(adjustedPrice);

        // Calculate risk level
        checklist.calculateRiskLevel();

        DiagnosticChecklist saved = diagnosticChecklistRepository.save(checklist);
        log.info("Diagnostic checklist created for estimation {}", createDTO.getEstimationId());

        return diagnosticChecklistMapper.toDTO(saved);
    }

    public PurchasePriceCalculationDTO calculatePurchasePrice(Long checklistId, Integer targetMarginPct) {
        DiagnosticChecklist checklist = diagnosticChecklistRepository.findById(checklistId)
                .orElseThrow(() -> new ResourceNotFoundException("Diagnostic checklist not found"));

        BigDecimal estimatedPrice = checklist.getEstimation().getEstimatedPrice();
        BigDecimal deductionsTotal = checklist.getDeductionsTotal();
        BigDecimal adjustedPrice = checklist.getAdjustedPrice();
        BigDecimal refurbishingCost = new BigDecimal("30"); // Default refurb cost

        // Calculate recommended purchase price
        BigDecimal recommendedPurchasePrice = PriceCalculator.calculateRecommendedPurchasePrice(
                estimatedPrice,
                deductionsTotal,
                refurbishingCost,
                targetMarginPct
        );

        // Calculate projected sell price
        BigDecimal projectedSellPrice = adjustedPrice.subtract(refurbishingCost);

        // Calculate projected margin
        BigDecimal projectedMargin = projectedSellPrice.subtract(recommendedPurchasePrice);
        BigDecimal projectedMarginPct = PriceCalculator.calculateMarginPercentage(
                recommendedPurchasePrice,
                projectedSellPrice
        );

        // Determine decision
        String decision;
        String reasoning;

        if (checklist.hasBlockers()) {
            decision = "UNPROFITABLE";
            reasoning = "Le produit contient des bloquants (iCloud Lock, IMEI blacklisté, etc.)";
        } else if (projectedMarginPct.compareTo(new BigDecimal("50")) < 0) {
            decision = "LOW_MARGIN";
            reasoning = "Marge projetée inférieure à 50%, négocier avec le fournisseur";
        } else {
            decision = "PROFITABLE";
            reasoning = "Achat rentable avec marge " + projectedMarginPct.intValue() + "%";
        }

        return PurchasePriceCalculationDTO.builder()
                .estimatedPrice(estimatedPrice)
                .deductionsTotal(deductionsTotal)
                .adjustedPrice(adjustedPrice)
                .refurbishingCost(refurbishingCost)
                .targetMarginPct(new BigDecimal(targetMarginPct))
                .targetMarginAmount(projectedMargin)
                .recommendedPurchasePrice(recommendedPurchasePrice)
                .projectedSellPrice(projectedSellPrice)
                .projectedMargin(projectedMargin)
                .projectedMarginPct(projectedMarginPct)
                .decision(decision)
                .reasoning(reasoning)
                .approved(!decision.equals("UNPROFITABLE"))
                .build();
    }

    private BigDecimal calculateDeductions(Map<String, Object> hardwareTests) {
        BigDecimal total = BigDecimal.ZERO;

        // deduction logic
        if (hardwareTests.containsKey("battery") && hardwareTests.get("battery") instanceof Map) {
            Map<String, Object> battery = (Map<String, Object>) hardwareTests.get("battery");
            if (battery.containsKey("deduction")) {
                total = total.add(new BigDecimal(battery.get("deduction").toString()));
            }
        }

        if (hardwareTests.containsKey("screen") && hardwareTests.get("screen") instanceof Map) {
            Map<String, Object> screen = (Map<String, Object>) hardwareTests.get("screen");
            if (screen.containsKey("deduction")) {
                total = total.add(new BigDecimal(screen.get("deduction").toString()));
            }
        }

        if (hardwareTests.containsKey("camera") && hardwareTests.get("camera") instanceof Map) {
            Map<String, Object> camera = (Map<String, Object>) hardwareTests.get("camera");
            if (camera.containsKey("deduction")) {
                total = total.add(new BigDecimal(camera.get("deduction").toString()));
            }
        }

        if (hardwareTests.containsKey("audio") && hardwareTests.get("audio") instanceof Map) {
            Map<String, Object> audio = (Map<String, Object>) hardwareTests.get("audio");
            if (audio.containsKey("deduction")) {
                total = total.add(new BigDecimal(audio.get("deduction").toString()));
            }
        }

        return total;
    }
}