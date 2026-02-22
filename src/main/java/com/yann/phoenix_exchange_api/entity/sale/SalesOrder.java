package com.yann.phoenix_exchange_api.entity.sale;

import com.yann.phoenix_exchange_api.entity.product.Product;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sales_orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalesOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_number", unique = true, nullable = false, length = 100)
    private String orderNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "total_amount", precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "tax_amount", precision = 10, scale = 2)
    private BigDecimal taxAmount;

    @Column(name = "discount_amount", precision = 10, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50)
    private SalesOrderStatus status = SalesOrderStatus.DRAFT;

    @Column(name = "payment_method", length = 50)
    private String paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", length = 50)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;

    @Column(name = "delivered_at")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime deliveredAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Payment> payments = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Business methods
    public void addItem(Product product, Integer quantity, BigDecimal unitPrice) {
        OrderItem item = OrderItem.builder()
                .order(this)
                .product(product)
                .quantity(quantity)
                .unitPrice(unitPrice)
                .totalPrice(unitPrice.multiply(new BigDecimal(quantity)))
                .build();

        this.orderItems.add(item);
        recalculateTotal();
    }

    public void recalculateTotal() {
        this.totalAmount = orderItems.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Appliquer réduction client VIP
        if (customer.isVIP()) {
            this.discountAmount = totalAmount.multiply(customer.applyDiscount());
            this.totalAmount = totalAmount.subtract(discountAmount);
        }

        // Calculer TVA (21% en Belgique)
        this.taxAmount = totalAmount.multiply(new BigDecimal("0.21"));
    }

    public void applyDiscount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
        this.totalAmount = this.totalAmount.subtract(discountAmount);
    }

    public void ship() {
        if (this.paymentStatus != PaymentStatus.PAID) {
            throw new IllegalStateException("Order must be PAID before shipping");
        }
        this.status = SalesOrderStatus.SHIPPED;
    }

    public void deliver() {
        if (this.status != SalesOrderStatus.SHIPPED) {
            throw new IllegalStateException("Order must be SHIPPED before delivery");
        }
        this.status = SalesOrderStatus.DELIVERED;
        this.deliveredAt = LocalDateTime.now();
    }

    public void cancel() {
        if (this.status == SalesOrderStatus.DELIVERED) {
            throw new IllegalStateException("Cannot cancel DELIVERED orders");
        }
        this.status = SalesOrderStatus.CANCELLED;

        // Libérer les produits réservés
        for (OrderItem item : orderItems) {
            Product product = item.getProduct();
            if (product.getStatus() == ProductStatus.RESERVED) {
                product.setStatus(ProductStatus.IN_STOCK);
            }
        }
    }
}
