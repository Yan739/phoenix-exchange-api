package com.yann.phoenix_exchange_api.entity.inventory;

import com.yann.phoenix_exchange_api.entity.product.Product;
import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "warehouses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Warehouse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "location", length = 255)
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 50)
    private WarehouseType type;

    @Column(name = "capacity")
    private Integer capacity;

    @Column(name = "current_stock")
    private Integer currentStock = 0;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @OneToMany(mappedBy = "warehouse", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Product> products = new ArrayList<>();

    @OneToMany(mappedBy = "warehouse", cascade = CascadeType.ALL)
    @Builder.Default
    private List<InventoryMovement> inventoryMovements = new ArrayList<>();

    // Business methods
    public boolean isFull() {
        return capacity != null && currentStock >= capacity;
    }

    public boolean canAddStock(Integer quantity) {
        if (capacity == null) return true;
        return (currentStock + quantity) <= capacity;
    }

    public void addStock(Integer quantity) {
        this.currentStock += quantity;
    }

    public void removeStock(Integer quantity) {
        this.currentStock = Math.max(0, this.currentStock - quantity);
    }

    public Double getOccupancyRate() {
        if (capacity == null || capacity == 0) return 0.0;
        return (double) currentStock / capacity * 100;
    }
}