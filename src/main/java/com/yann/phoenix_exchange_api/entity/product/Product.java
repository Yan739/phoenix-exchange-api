package com.yann.phoenix_exchange_api.entity.product;

import com.yann.phoenix_exchange_api.entity.inventory.InventoryMovement;
import com.yann.phoenix_exchange_api.entity.inventory.Warehouse;
import com.yann.phoenix_exchange_api.entity.purchase.PurchaseOrder;
import com.yann.phoenix_exchange_api.entity.purchase.Supplier;
import com.yann.phoenix_exchange_api.entity.repair.RepairTicket;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    @Builder.Default
    private List<RepairTicket> repairTickets = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    @Builder.Default
    private List<InventoryMovement> inventoryMovements = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Business methods
    public boolean isAvailableForSale() {
        return status == ProductStatus.IN_STOCK || status == ProductStatus.REFURBISHED;
    }

    public BigDecimal calculateMargin() {
        if (purchasePrice == null || sellPrice == null) {
            return BigDecimal.ZERO;
        }
        return sellPrice.subtract(purchasePrice);
    }

    public BigDecimal calculateMarginPercentage() {
        if (purchasePrice == null || purchasePrice.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return calculateMargin()
                .divide(purchasePrice, 4, BigDecimal.ROUND_HALF_UP)
                .multiply(new BigDecimal("100"));
    }

    public void changeStatus(ProductStatus newStatus) {
        if (!isTransitionAllowed(this.status, newStatus)) {
            throw new IllegalStateException(
                    String.format("Transition from %s to %s is not allowed", this.status, newStatus)
            );
        }
        this.status = newStatus;
    }

    private boolean isTransitionAllowed(ProductStatus from, ProductStatus to) {
        if (from == null) return true;

        return switch (from) {
            case RECEIVED -> to == ProductStatus.IN_REPAIR;
            case IN_REPAIR -> to == ProductStatus.REFURBISHED;
            case REFURBISHED -> to == ProductStatus.IN_REPAIR || to == ProductStatus.IN_STOCK;
            case IN_STOCK -> to == ProductStatus.RESERVED;
            case RESERVED -> to == ProductStatus.IN_STOCK || to == ProductStatus.SOLD;
            case SOLD -> false;
        };
    }

    public void assignGrade(Grade newGrade) {
        if (this.status != ProductStatus.REFURBISHED) {
            throw new IllegalStateException("Grade can only be assigned to REFURBISHED products");
        }
        this.grade = newGrade;
    }
}