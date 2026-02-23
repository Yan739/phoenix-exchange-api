package com.yann.phoenix_exchange_api.dto.dashboard;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDTO {

    // === PRODUCTS ===
    private Integer totalProducts;
    private Integer productsInStock;
    private Integer productsInRepair;
    private Integer productsRefurbished;
    private Integer productsReserved;
    private Integer productsSold;
    private Integer productsSoldThisMonth;

    // === REPAIR TICKETS ===
    private Integer totalTickets;
    private Integer openTickets;
    private Integer ticketsInDiagnostic;
    private Integer ticketsInRepair;
    private Integer ticketsInTesting;
    private Integer urgentTickets;
    private Integer overdueTickets;
    private Double averageRepairDuration;

    // === SALES ===
    private BigDecimal totalRevenue;
    private BigDecimal revenueThisMonth;
    private BigDecimal revenueLastMonth;
    private BigDecimal revenueThisYear;
    private Integer totalOrders;
    private Integer ordersThisMonth;
    private Integer ordersPending;
    private Integer ordersShipped;
    private BigDecimal averageOrderValue;

    // === PURCHASES ===
    private BigDecimal totalPurchaseCost;
    private BigDecimal purchaseCostThisMonth;
    private BigDecimal purchaseCostLastMonth;
    private Integer totalPurchaseOrders;
    private Integer purchaseOrdersPending;
    private Integer purchaseOrdersApproved;

    // === MARGINS ===
    private BigDecimal totalMargin;
    private BigDecimal averageMargin;
    private BigDecimal averageMarginPct;
    private BigDecimal marginThisMonth;

    // === CUSTOMERS ===
    private Integer totalCustomers;
    private Integer activeCustomers;
    private Integer vipCustomers;
    private Integer regularCustomers;
    private Integer newCustomersThisMonth;

    // === SUPPLIERS ===
    private Integer totalSuppliers;
    private Integer activeSuppliers;
    private Integer reliableSuppliers; // rating >= 4.0

    // === INVENTORY ===
    private Integer totalWarehouseCapacity;
    private Integer currentTotalStock;
    private Double averageOccupancyRate;
    private Integer warehousesNearCapacity; // > 80%

    // === SMART VALUATOR ===
    private Integer totalEstimations;
    private Integer estimationsThisMonth;
    private Integer diagnosticsCompleted;
    private Integer diagnosticsWithBlockers;

    // === DATES ===
    private LocalDateTime lastUpdated;
    private String period; // "TODAY", "THIS_MONTH", "THIS_YEAR"
}