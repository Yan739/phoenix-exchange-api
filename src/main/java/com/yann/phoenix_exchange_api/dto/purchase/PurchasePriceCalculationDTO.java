package com.yann.phoenix_exchange_api.dto.purchase;

import lombok.*;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchasePriceCalculationDTO {
    private BigDecimal estimatedPrice;
    private BigDecimal deductionsTotal;
    private BigDecimal adjustedPrice;
    private BigDecimal refurbishingCost;
    private BigDecimal targetMarginPct;
    private BigDecimal recommendedPurchasePrice;
    private BigDecimal projectedMargin;
    private BigDecimal projectedMarginPct;
    private String decision;
}