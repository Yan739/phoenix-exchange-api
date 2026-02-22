package com.yann.phoenix_exchange_api.domain.inventory;

import jakarta.persistence.*;
import lombok.*;

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

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private WarehouseType type;

    @Column(nullable = false)
    private int capacity;

    @Column(nullable = false)
    private int currentStock;

    @Column(nullable = false)
    private boolean isActive;

}
