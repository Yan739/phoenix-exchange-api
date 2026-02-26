package com.yann.phoenix_exchange_api.entity.sale;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private CustomerType type;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "phone", length = 50)
    private String phone;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @Column(name = "vat_number", length = 100)
    private String vatNumber;

    @Column(name = "loyalty_points")
    private Integer loyaltyPoints = 0;

    @Column(name = "total_spent", precision = 12, scale = 2)
    private BigDecimal totalSpent = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "segment", length = 50)
    private CustomerSegment segment;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    @Builder.Default
    private List<SalesOrder> salesOrders = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (segment == null) {
            segment = CustomerSegment.NEW;
        }
    }

    // Business methods
    public void addLoyaltyPoints(Integer points) {
        this.loyaltyPoints += points;
    }

    public void addSpending(BigDecimal amount) {
        this.totalSpent = this.totalSpent.add(amount);
        updateSegment();
    }

    public void updateSegment() {
        if (totalSpent.compareTo(new BigDecimal("5000")) >= 0) {
            this.segment = CustomerSegment.VIP;
        } else if (totalSpent.compareTo(new BigDecimal("500")) >= 0) {
            this.segment = CustomerSegment.REGULAR;
        } else {
            this.segment = CustomerSegment.NEW;
        }
    }

    public boolean isVIP() {
        return segment == CustomerSegment.VIP;
    }

    public BigDecimal applyDiscount() {
        if (isVIP()) {
            return new BigDecimal("0.05");
        }
        return BigDecimal.ZERO;
    }
}