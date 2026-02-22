package com.yann.phoenix_exchange_api.entity.sale;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private SalesOrder order;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "method", length = 50)
    private PaymentMethod method;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50)
    private PaymentStatus status = PaymentStatus.PENDING;

    @Column(name = "transaction_id", length = 255)
    private String transactionId;

    @Column(name = "reference", length = 255)
    private String reference;

    @Column(name = "paid_at")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime paidAt;

    @Column(name = "created_at", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Business methods
    public void process(String transactionId) {
        if (this.status != PaymentStatus.PENDING) {
            throw new IllegalStateException("Only PENDING payments can be processed");
        }
        this.status = PaymentStatus.COMPLETED;
        this.transactionId = transactionId;
        this.paidAt = LocalDateTime.now();

        // update order and payment status
        order.setPaymentStatus(PaymentStatus.PAID);
        order.setStatus(SalesOrderStatus.PAID);
    }

    public void fail() {
        this.status = PaymentStatus.FAILED;
    }

    public void refund() {
        if (this.status != PaymentStatus.COMPLETED) {
            throw new IllegalStateException("Only COMPLETED payments can be refunded");
        }
        this.status = PaymentStatus.REFUNDED;
    }

    public boolean isPaid() {
        return this.status == PaymentStatus.COMPLETED;
    }
}
