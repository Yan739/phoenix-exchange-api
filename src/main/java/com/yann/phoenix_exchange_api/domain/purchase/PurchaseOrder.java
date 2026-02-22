package com.yann.phoenix_exchange_api.domain.purchase;

import com.yann.phoenix_exchange_api.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "purchase_orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orderNumber;

    @OneToOne
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(nullable = false, updatable = false)
    private String notes;

    @OneToOne
    @JoinColumn(name = "created_by_id")
    private User createdBy;

    @OneToOne
    @JoinColumn(name = "approved_by_id")
    private User approvedBy;

    @Column(nullable = false, updatable = false)
    private LocalDateTime receivedAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

}
