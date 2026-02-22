package com.yann.phoenix_exchange_api.domain.sale;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

import java.time.LocalDateTime;
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

    @Column(nullable = false)
    private CustomerType customerType;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String vatNumber;

    @Column(nullable = false)
    private int loyalityPoints;

    @Column(nullable = false)
    private BigDecimal totalSpent;

    @Column(nullable = false)
    private CustomerSegment customerSegment;

    @Column(nullable = false)
    private boolean isActive;

    @OneToMany(mappedBy = "customer")
    private List<SalesOrder> salesOrders;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

}
