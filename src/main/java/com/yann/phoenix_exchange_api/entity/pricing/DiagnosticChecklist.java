package com.yann.phoenix_exchange_api.entity.pricing;

import com.yann.phoenix_exchange_api.entity.product.Product;
import com.yann.phoenix_exchange_api.entity.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "diagnostic_checklists")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiagnosticChecklist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estimation_id")
    private SmartValuatorEstimation estimation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "technician_id")
    private User technician;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "blockers", columnDefinition = "jsonb")
    private Map<String, Boolean> blockers;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "hardware_tests", columnDefinition = "jsonb")
    private Map<String, Object> hardwareTests;

    @Column(name = "deductions_total", precision = 10, scale = 2)
    private BigDecimal deductionsTotal = BigDecimal.ZERO;

    @Column(name = "adjusted_price", precision = 10, scale = 2)
    private BigDecimal adjustedPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_level", length = 20)
    private RiskLevel riskLevel;

    @Column(name = "created_at", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Business methods
    public boolean hasBlockers() {
        if (blockers == null) return false;
        return blockers.values().stream().anyMatch(blocked -> blocked);
    }

    public BigDecimal calculateAdjustedPrice(BigDecimal basePrice) {
        if (basePrice == null) return BigDecimal.ZERO;
        return basePrice.subtract(deductionsTotal);
    }

    public BigDecimal calculatePurchasePrice(BigDecimal adjustedPrice, BigDecimal refurbCost, BigDecimal targetMargin) {
        // Formul: (Adjusted Price - Refurb Cost) / (1 + Target Margin)
        if (adjustedPrice == null) return BigDecimal.ZERO;

        BigDecimal numerator = adjustedPrice.subtract(refurbCost != null ? refurbCost : BigDecimal.ZERO);
        BigDecimal denominator = BigDecimal.ONE.add(targetMargin);

        return numerator.divide(denominator, 2, BigDecimal.ROUND_HALF_UP);
    }

    public void calculateRiskLevel() {
        if (hasBlockers()) {
            this.riskLevel = RiskLevel.BLOCKER;
        } else if (deductionsTotal.compareTo(new BigDecimal("100")) > 0) {
            this.riskLevel = RiskLevel.HIGH;
        } else if (deductionsTotal.compareTo(new BigDecimal("30")) > 0) {
            this.riskLevel = RiskLevel.MODERATE;
        } else {
            this.riskLevel = RiskLevel.LOW;
        }
    }

    public boolean isRentable(BigDecimal purchasePrice, BigDecimal minimumMargin) {
        if (hasBlockers()) return false;

        BigDecimal potentialMargin = adjustedPrice.subtract(purchasePrice);
        BigDecimal marginPercentage = potentialMargin
                .divide(purchasePrice, 4, BigDecimal.ROUND_HALF_UP)
                .multiply(new BigDecimal("100"));

        return marginPercentage.compareTo(minimumMargin) >= 0;
    }
}
