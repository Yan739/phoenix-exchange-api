package com.yann.phoenix_exchange_api.entity.pricing;

import com.yann.phoenix_exchange_api.entity.product.Grade;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "pricing_rules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PricingRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "category", length = 100)
    private String category;

    @Enumerated(EnumType.STRING)
    @Column(name = "grade", length = 5)
    private Grade grade;

    @Column(name = "target_margin_pct")
    private Integer targetMarginPct;

    @Column(name = "refurbishing_cost_avg", precision = 10, scale = 2)
    private BigDecimal refurbishingCostAvg;

    @Column(name = "is_active")
    private Boolean isActive = true;

    // Business methods
    public BigDecimal getTargetMarginDecimal() {
        if (targetMarginPct == null) return new BigDecimal("0.5");
        return new BigDecimal(targetMarginPct).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP);
    }

    public BigDecimal calculateSellPrice(BigDecimal purchasePrice) {
        // Sell Price = Purchase Price × (1 + Target Margin) + Refurb Cost
        if (purchasePrice == null) return BigDecimal.ZERO;

        BigDecimal margin = BigDecimal.ONE.add(getTargetMarginDecimal());
        BigDecimal basePrice = purchasePrice.multiply(margin);

        if (refurbishingCostAvg != null) {
            basePrice = basePrice.add(refurbishingCostAvg);
        }

        return basePrice;
    }

    public BigDecimal calculatePurchasePrice(BigDecimal sellPrice) {
        // Purchase Price = (Sell Price - Refurb Cost) / (1 + Target Margin)
        if (sellPrice == null) return BigDecimal.ZERO;

        BigDecimal adjustedSellPrice = sellPrice;
        if (refurbishingCostAvg != null) {
            adjustedSellPrice = sellPrice.subtract(refurbishingCostAvg);
        }

        BigDecimal divisor = BigDecimal.ONE.add(getTargetMarginDecimal());
        return adjustedSellPrice.divide(divisor, 2, BigDecimal.ROUND_HALF_UP);
    }
}