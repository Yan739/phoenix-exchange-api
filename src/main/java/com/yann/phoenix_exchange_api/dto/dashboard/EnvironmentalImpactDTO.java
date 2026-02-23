package com.yann.phoenix_exchange_api.dto.dashboard;

import lombok.*;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnvironmentalImpactDTO {

    // Products refurbished
    private Integer productsRefurbished;
    private Integer productsRefurbishedThisMonth;
    private Integer productsRefurbishedThisYear;

    // Waste avoided (kg)
    private BigDecimal wasteAvoided;
    private BigDecimal wasteAvoidedThisMonth;
    private BigDecimal wasteAvoidedThisYear;
    private BigDecimal averageProductWeight;

    // CO2 emissions avoided (kg)
    private BigDecimal co2Avoided;
    private BigDecimal co2AvoidedThisMonth;
    private BigDecimal co2AvoidedThisYear;
    private BigDecimal co2PerProduct;

    // Water saved (liters)
    private BigDecimal waterSaved;
    private BigDecimal waterSavedThisMonth;
    private BigDecimal waterSavedThisYear;

    // Energy saved (kWh)
    private BigDecimal energySaved;
    private BigDecimal energySavedThisMonth;
    private BigDecimal energySavedThisYear;

    // Equivalents (for communication)
    private Integer treesEquivalent;
    private Integer carsOffRoadDays;
    private Integer householdsWaterSupply;

    // Certifications
    private String certificationLevel; // "Bronze", "Silver", "Gold", "Platinum"
    private Integer pointsToNextLevel;
}
