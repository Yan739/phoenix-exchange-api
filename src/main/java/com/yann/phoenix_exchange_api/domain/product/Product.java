package com.yann.phoenix_exchange_api.domain.product;

import com.yann.phoenix_exchange_api.domain.inventory.Warehouse;
import com.yann.phoenix_exchange_api.domain.purchase.PurchaseOrder;
import com.yann.phoenix_exchange_api.domain.purchase.Supplier;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "serial_number", unique = true, nullable = false, length = 255)
    private String serialNumber;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "brand", length = 100)
    private String brand;

    @Column(name = "category", length = 100)
    private String category;

    @Column(name = "purchase_price", precision = 10, scale = 2)
    private BigDecimal purchasePrice;

    @Column(name = "sell_price", precision = 10, scale = 2)
    private BigDecimal sellPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50)
    private ProductStatus status = ProductStatus.RECEIVED;

    @Enumerated(EnumType.STRING)
    @Column(name = "grade", length = 5)
    private Grade grade;

    @Column(name = "year")
    private Integer year;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_order_id")
    private PurchaseOrder purchaseOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id")
    private Warehouse warehouse;

    @Column(name = "created_at", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public boolean isAvailableForSale() {
        return status == ProductStatus.IN_STOCK || status == ProductStatus.REFURBISHED;
    }

    public BigDecimal calculateMargin() {
        if (purchasePrice == null || sellPrice == null) return BigDecimal.ZERO;
        return sellPrice.subtract(purchasePrice);
    }
}
