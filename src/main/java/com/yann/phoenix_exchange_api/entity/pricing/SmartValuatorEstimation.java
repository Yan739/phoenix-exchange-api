package com.yann.phoenix_exchange_api.entity.pricing;

import com.yann.phoenix_exchange_api.entity.product.Grade;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "smart_valuator_estimations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SmartValuatorEstimation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "item_name", nullable = false, length = 255)
    private String itemName;

    @Column(name = "brand", length = 100)
    private String brand;

    @Column(name = "category", length = 100)
    private String category;

    @Column(name = "year")
    private Integer year;

    @Column(name = "condition_rating")
    private Integer conditionRating;

    @Column(name = "estimated_price", precision = 10, scale = 2)
    private BigDecimal estimatedPrice;

    @Column(name = "ai_description", columnDefinition = "TEXT")
    private String aiDescription;

    @Column(name = "confidence")
    private Integer confidence;

    @Column(name = "created_at", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Business methods
    public Grade suggestGrade() {
        if (conditionRating >= 9) return Grade.A_PLUS;
        if (conditionRating >= 7) return Grade.A;
        if (conditionRating >= 5) return Grade.B;
        return Grade.C;
    }

    public BigDecimal calculateFallbackPrice(BigDecimal basePrice) {
        // Fallback pricing: Base × (note/10)
        if (basePrice == null || conditionRating == null) {
            return BigDecimal.ZERO;
        }

        double factor = conditionRating / 10.0;
        return basePrice.multiply(new BigDecimal(factor));
    }

    public boolean isHighConfidence() {
        return confidence != null && confidence >= 80;
    }
}
