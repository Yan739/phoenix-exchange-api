package com.yann.phoenix_exchange_api.entity.purchase;

import com.yann.phoenix_exchange_api.entity.user.User;

import com.yann.phoenix_exchange_api.entity.product.Product;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @Column(name = "order_number", unique = true, nullable = false, length = 100)
    private String orderNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @Column(name = "total_amount", precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50)
    private OrderStatus status = OrderStatus.DRAFT;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    @Column(name = "received_at")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime receivedAt;

    @Column(name = "created_at", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "purchaseOrder", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Product> products = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Business methods
    public void approve(User approver) {
        if (this.status != OrderStatus.PENDING) {
            throw new IllegalStateException("Only PENDING orders can be approved");
        }
        this.status = OrderStatus.APPROVED;
        this.approvedBy = approver;
    }

    public void receive() {
        if (this.status != OrderStatus.APPROVED) {
            throw new IllegalStateException("Only APPROVED orders can be received");
        }
        this.status = OrderStatus.RECEIVED;
        this.receivedAt = LocalDateTime.now();
    }

    public void cancel() {
        if (this.status == OrderStatus.RECEIVED) {
            throw new IllegalStateException("Cannot cancel RECEIVED orders");
        }
        this.status = OrderStatus.CANCELLED;
    }

    public boolean requiresManagerApproval() {
        return totalAmount != null && totalAmount.compareTo(new BigDecimal("5000")) > 0;
    }
}