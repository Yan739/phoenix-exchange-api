package com.yann.phoenix_exchange_api.service;

import com.yann.phoenix_exchange_api.dto.dashboard.*;
import com.yann.phoenix_exchange_api.entity.inventory.Warehouse;
import com.yann.phoenix_exchange_api.entity.product.Grade;
import com.yann.phoenix_exchange_api.entity.product.ProductStatus;
import com.yann.phoenix_exchange_api.entity.purchase.OrderStatus;
import com.yann.phoenix_exchange_api.entity.repair.Priority;
import com.yann.phoenix_exchange_api.entity.repair.TicketStatus;
import com.yann.phoenix_exchange_api.entity.sale.CustomerSegment;
import com.yann.phoenix_exchange_api.entity.sale.SalesOrderStatus;
import com.yann.phoenix_exchange_api.repository.*;
import com.yann.phoenix_exchange_api.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DashboardService {

    private final ProductRepository productRepository;
    private final RepairTicketRepository repairTicketRepository;
    private final SalesOrderRepository salesOrderRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final CustomerRepository customerRepository;
    private final SupplierRepository supplierRepository;
    private final WarehouseRepository warehouseRepository;
    private final SmartValuatorEstimationRepository estimationRepository;
    private final DiagnosticChecklistRepository diagnosticChecklistRepository;

    /**
     * Get complete dashboard statistics
     */
    public DashboardStatsDTO getDashboardStats() {
        LocalDateTime startOfMonth = DateUtils.getStartOfMonth();
        LocalDateTime endOfMonth = DateUtils.getEndOfMonth();
        LocalDateTime startOfLastMonth = DateUtils.getStartOfLastMonth();
        LocalDateTime endOfLastMonth = DateUtils.getEndOfLastMonth();
        LocalDateTime startOfYear = DateUtils.getStartOfYear();

        return DashboardStatsDTO.builder()
                // Products
                .totalProducts((int)productRepository.count())
                .productsInStock(productRepository.countByStatus(ProductStatus.IN_STOCK).intValue())
                .productsInRepair(productRepository.countByStatus(ProductStatus.IN_REPAIR).intValue())
                .productsRefurbished(productRepository.countByStatus(ProductStatus.REFURBISHED).intValue())
                .productsReserved(productRepository.countByStatus(ProductStatus.RESERVED).intValue())
                .productsSold(productRepository.countByStatus(ProductStatus.SOLD).intValue())
                .productsSoldThisMonth(countProductsSoldInPeriod(startOfMonth, endOfMonth))

                // Repair Tickets
                .totalTickets((int)repairTicketRepository.count())
                .openTickets(getOpenTicketsCount())
                .ticketsInDiagnostic(repairTicketRepository.countByStatus(TicketStatus.DIAGNOSTIC).intValue())
                .ticketsInRepair(repairTicketRepository.countByStatus(TicketStatus.IN_REPAIR).intValue())
                .ticketsInTesting(repairTicketRepository.countByStatus(TicketStatus.TESTING).intValue())
                .urgentTickets(countUrgentTickets())
                .overdueTickets(countOverdueTickets())
                .averageRepairDuration(calculateAverageRepairDuration())

                // Sales
                .totalRevenue(calculateTotalRevenue())
                .revenueThisMonth(salesOrderRepository.calculateRevenueBetween(startOfMonth, endOfMonth))
                .revenueLastMonth(salesOrderRepository.calculateRevenueBetween(startOfLastMonth, endOfLastMonth))
                .revenueThisYear(salesOrderRepository.calculateRevenueBetween(startOfYear, LocalDateTime.now()))
                .totalOrders((int)salesOrderRepository.count())
                .ordersThisMonth(countOrdersInPeriod(startOfMonth, endOfMonth))
                .ordersPending(salesOrderRepository.countByStatus(SalesOrderStatus.PENDING_PAYMENT).intValue())
                .ordersShipped(salesOrderRepository.countByStatus(SalesOrderStatus.SHIPPED).intValue())
                .averageOrderValue(calculateAverageOrderValue())

                // Purchases
                .totalPurchaseCost(calculateTotalPurchaseCost())
                .purchaseCostThisMonth(calculatePurchaseCostInPeriod(startOfMonth, endOfMonth))
                .purchaseCostLastMonth(calculatePurchaseCostInPeriod(startOfLastMonth, endOfLastMonth))
                .totalPurchaseOrders((int)purchaseOrderRepository.count())
                .purchaseOrdersPending(purchaseOrderRepository.findByStatus(OrderStatus.PENDING).size())
                .purchaseOrdersApproved(purchaseOrderRepository.findByStatus(OrderStatus.APPROVED).size())

                // Margins
                .totalMargin(calculateTotalMargin())
                .averageMargin(calculateAverageMargin())
                .averageMarginPct(calculateAverageMarginPercentage())
                .marginThisMonth(calculateMarginInPeriod(startOfMonth, endOfMonth))

                // Customers
                .totalCustomers((int)customerRepository.count())
                .activeCustomers(customerRepository.findByIsActive(true).size())
                .vipCustomers(customerRepository.findVIPCustomers().size())
                .regularCustomers(customerRepository.findBySegment(CustomerSegment.REGULAR).size())
                .newCustomersThisMonth(countNewCustomersInPeriod(startOfMonth, endOfMonth))

                // Suppliers
                .totalSuppliers((int)supplierRepository.count())
                .activeSuppliers(supplierRepository.findByIsActive(true).size())
                .reliableSuppliers(supplierRepository.findByRatingGreaterThanEqual(new BigDecimal("4.0")).size())

                // Inventory
                .totalWarehouseCapacity(calculateTotalWarehouseCapacity())
                .currentTotalStock(calculateCurrentTotalStock())
                .averageOccupancyRate(calculateAverageOccupancyRate())
                .warehousesNearCapacity(countWarehousesNearCapacity())

                // Smart Valuator
                .totalEstimations((int)estimationRepository.count())
                .estimationsThisMonth(countEstimationsInPeriod(startOfMonth, endOfMonth))
                .diagnosticsCompleted((int)diagnosticChecklistRepository.count())
                .diagnosticsWithBlockers(countDiagnosticsWithBlockers())

                // Metadata
                .lastUpdated(LocalDateTime.now())
                .period("THIS_MONTH")
                .build();
    }

    /**
     * Get KPI metrics
     */
    public List<KPIResponseDTO> getKPIs() {
        List<KPIResponseDTO> kpis = new ArrayList<>();

        LocalDateTime startOfMonth = DateUtils.getStartOfMonth();
        LocalDateTime endOfMonth = DateUtils.getEndOfMonth();
        LocalDateTime startOfLastMonth = DateUtils.getStartOfLastMonth();
        LocalDateTime endOfLastMonth = DateUtils.getEndOfLastMonth();

        // Revenue KPI
        BigDecimal revenueThisMonth = salesOrderRepository.calculateRevenueBetween(startOfMonth, endOfMonth);
        BigDecimal revenueLastMonth = salesOrderRepository.calculateRevenueBetween(startOfLastMonth, endOfLastMonth);
        kpis.add(createKPI(
                "Chiffre d'affaires",
                "Revenu total ce mois",
                revenueThisMonth,
                "€",
                revenueLastMonth,
                new BigDecimal("50000"), // Target
                "SUCCESS"
        ));

        // Margin KPI
        BigDecimal averageMarginPct = calculateAverageMarginPercentage();
        kpis.add(createKPI(
                "Marge moyenne",
                "Marge sur les ventes",
                averageMarginPct,
                "%",
                new BigDecimal("75"),
                new BigDecimal("80"), // Target
                averageMarginPct.compareTo(new BigDecimal("70")) >= 0 ? "SUCCESS" : "WARNING"
        ));

        // Products sold KPI
        Integer productsSoldThisMonth = countProductsSoldInPeriod(startOfMonth, endOfMonth);
        kpis.add(createKPI(
                "Produits vendus",
                "Ce mois",
                new BigDecimal(productsSoldThisMonth),
                "unités",
                new BigDecimal(countProductsSoldInPeriod(startOfLastMonth, endOfLastMonth)),
                new BigDecimal("100"), // Target
                "INFO"
        ));

        // Open tickets KPI
        Integer openTickets = getOpenTicketsCount();
        kpis.add(createKPI(
                "Tickets ouverts",
                "En cours de réparation",
                new BigDecimal(openTickets),
                "tickets",
                null,
                new BigDecimal("50"), // Target max
                openTickets > 50 ? "WARNING" : "SUCCESS"
        ));

        return kpis;
    }

    /**
     * Get revenue chart data
     */
    public RevenueChartDTO getRevenueChart(String period) {
        // TODO: Implement based on period (daily, weekly, monthly, yearly)
        return RevenueChartDTO.builder()
                .period(period)
                .startDate(DateUtils.getStartOfMonth().toLocalDate())
                .endDate(LocalDateTime.now().toLocalDate())
                .dataPoints(new ArrayList<>())
                .totalRevenue(calculateTotalRevenue())
                .averageRevenue(calculateAverageOrderValue())
                .build();
    }

    /**
     * Get product statistics
     */
    public ProductStatsDTO getProductStats() {
        Map<ProductStatus, Integer> byStatus = new EnumMap<>(ProductStatus.class);
        for (ProductStatus status : ProductStatus.values()) {
            byStatus.put(status, productRepository.countByStatus(status).intValue());
        }

        Map<Grade, Integer> byGrade = new EnumMap<>(Grade.class);
        for (Grade grade : Grade.values()) {
            byGrade.put(grade, productRepository.countByGrade(grade).intValue());
        }

        return ProductStatsDTO.builder()
                .totalProducts((int)productRepository.count())
                .productsByStatus(byStatus)
                .productsByGrade(byGrade)
                .productsByCategory(new HashMap<>()) // TODO: Implement
                .topBrands(new HashMap<>()) // TODO: Implement
                .averageDaysInStock(30.0) // TODO: Calculate
                .productsTurnedOverThisMonth(countProductsSoldInPeriod(
                        DateUtils.getStartOfMonth(),
                        DateUtils.getEndOfMonth()
                ))
                .inventoryTurnoverRate(2.5) // TODO: Calculate
                .build();
    }

    /**
     * Get repair statistics
     */
    public RepairStatsDTO getRepairStats() {
        Map<TicketStatus, Integer> byStatus = new EnumMap<>(TicketStatus.class);
        for (TicketStatus status : TicketStatus.values()) {
            byStatus.put(status, repairTicketRepository.countByStatus(status).intValue());
        }

        return RepairStatsDTO.builder()
                .totalTickets((int)repairTicketRepository.count())
                .openTickets(getOpenTicketsCount())
                .closedTickets(repairTicketRepository.countByStatus(TicketStatus.CLOSED).intValue())
                .ticketsByStatus(byStatus)
                .ticketsByPriority(new EnumMap<>(Priority.class)) // TODO: Implement
                .averageRepairDuration(calculateAverageRepairDuration())
                .averageRepairCost(50.0) // TODO: Calculate
                .totalRepairCosts(BigDecimal.ZERO) // TODO: Calculate
                .ticketsClosedThisMonth(countTicketsClosedInPeriod(
                        DateUtils.getStartOfMonth(),
                        DateUtils.getEndOfMonth()
                ))
                .costVariancePercentage(5.0) // TODO: Calculate
                .durationVariancePercentage(10.0) // TODO: Calculate
                .overdueTickets(countOverdueTickets())
                .urgentUnassignedTickets(countUrgentTickets())
                .build();
    }

    // ========================================
    // HELPER METHODS
    // ========================================

    private KPIResponseDTO createKPI(String label, String description, BigDecimal value,
                                     String unit, BigDecimal previousValue,
                                     BigDecimal targetValue, String status) {
        BigDecimal change = null;
        Double changePercentage = null;
        String trend = "STABLE";

        if (previousValue != null && previousValue.compareTo(BigDecimal.ZERO) > 0) {
            change = value.subtract(previousValue);
            changePercentage = change.divide(previousValue, 4, BigDecimal.ROUND_HALF_UP)
                    .multiply(new BigDecimal("100")).doubleValue();

            if (changePercentage > 5) {
                trend = "UP";
            } else if (changePercentage < -5) {
                trend = "DOWN";
            }
        }

        Double progressToTarget = null;
        Boolean targetReached = false;

        if (targetValue != null && targetValue.compareTo(BigDecimal.ZERO) > 0) {
            progressToTarget = value.divide(targetValue, 4, BigDecimal.ROUND_HALF_UP)
                    .multiply(new BigDecimal("100")).doubleValue();
            targetReached = value.compareTo(targetValue) >= 0;
        }

        return KPIResponseDTO.builder()
                .label(label)
                .description(description)
                .value(value)
                .unit(unit)
                .formattedValue(formatValue(value, unit))
                .previousValue(previousValue)
                .change(change)
                .changePercentage(changePercentage)
                .trend(trend)
                .status(status)
                .targetValue(targetValue)
                .progressToTarget(progressToTarget)
                .targetReached(targetReached)
                .build();
    }

    private String formatValue(BigDecimal value, String unit) {
        if ("€".equals(unit)) {
            return String.format("%.2f €", value);
        } else if ("%".equals(unit)) {
            return String.format("%.1f%%", value);
        } else {
            return String.format("%.0f %s", value, unit);
        }
    }

    private Integer getOpenTicketsCount() {
        return repairTicketRepository.countByStatus(TicketStatus.RECEIVED).intValue() +
                repairTicketRepository.countByStatus(TicketStatus.DIAGNOSTIC).intValue() +
                repairTicketRepository.countByStatus(TicketStatus.IN_REPAIR).intValue() +
                repairTicketRepository.countByStatus(TicketStatus.TESTING).intValue();
    }

    private Integer countUrgentTickets() {
        return repairTicketRepository.findUnassignedByStatus(TicketStatus.RECEIVED).stream()
                .filter(ticket -> ticket.getPriority() == Priority.URGENT)
                .toList()
                .size();
    }

    private Integer countOverdueTickets() {
        return (int) repairTicketRepository.findAll().stream()
                .filter(ticket -> ticket.isOverdue())
                .count();
    }

    private Double calculateAverageRepairDuration() {
        // TODO: Implement actual calculation
        return 3.5; // days
    }

    private BigDecimal calculateTotalRevenue() {
        return salesOrderRepository.calculateRevenueBetween(
                LocalDateTime.of(2020, 1, 1, 0, 0),
                LocalDateTime.now()
        );
    }

    private Integer countOrdersInPeriod(LocalDateTime start, LocalDateTime end) {
        return salesOrderRepository.findByCreatedAtBetween(start, end).size();
    }

    private BigDecimal calculateAverageOrderValue() {
        Long count = salesOrderRepository.count();
        if (count == 0) return BigDecimal.ZERO;

        BigDecimal totalRevenue = calculateTotalRevenue();
        return totalRevenue.divide(new BigDecimal(count), 2, BigDecimal.ROUND_HALF_UP);
    }

    private BigDecimal calculateTotalPurchaseCost() {
        // TODO: Implement
        return BigDecimal.ZERO;
    }

    private BigDecimal calculatePurchaseCostInPeriod(LocalDateTime start, LocalDateTime end) {
        // TODO: Implement
        return BigDecimal.ZERO;
    }

    private BigDecimal calculateTotalMargin() {
        // TODO: Implement
        return BigDecimal.ZERO;
    }

    private BigDecimal calculateAverageMargin() {
        // TODO: Implement
        return BigDecimal.ZERO;
    }

    private BigDecimal calculateAverageMarginPercentage() {
        // TODO: Implement real calculation
        return new BigDecimal("78.5");
    }

    private BigDecimal calculateMarginInPeriod(LocalDateTime start, LocalDateTime end) {
        // TODO: Implement
        return BigDecimal.ZERO;
    }

    private Integer countNewCustomersInPeriod(LocalDateTime start, LocalDateTime end) {
        return customerRepository.findAll().stream()
                .filter(customer -> DateUtils.isWithinRange(customer.getCreatedAt(), start, end))
                .toList()
                .size();
    }

    private Integer calculateTotalWarehouseCapacity() {
        return warehouseRepository.findAll().stream()
                .mapToInt(w -> w.getCapacity() != null ? w.getCapacity() : 0)
                .sum();
    }

    private Integer calculateCurrentTotalStock() {
        return warehouseRepository.findAll().stream()
                .mapToInt(w -> w.getCurrentStock() != null ? w.getCurrentStock() : 0)
                .sum();
    }

    private Double calculateAverageOccupancyRate() {
        List<Warehouse> warehouses = warehouseRepository.findAll();
        if (warehouses.isEmpty()) return 0.0;

        return warehouses.stream()
                .mapToDouble(w -> w.getOccupancyRate())
                .average()
                .orElse(0.0);
    }

    private Integer countWarehousesNearCapacity() {
        return (int) warehouseRepository.findAll().stream()
                .filter(w -> w.getOccupancyRate() > 80.0)
                .count();
    }

    private Integer countEstimationsInPeriod(LocalDateTime start, LocalDateTime end) {
        return estimationRepository.findByCreatedAtBetween(start, end).size();
    }

    private Integer countDiagnosticsWithBlockers() {
        return (int) diagnosticChecklistRepository.findAll().stream()
                .filter(d -> d.hasBlockers())
                .count();
    }

    private Integer countProductsSoldInPeriod(LocalDateTime start, LocalDateTime end) {
        // TODO: Implement by tracking when products changed to SOLD status
        return 0;
    }

    private Integer countTicketsClosedInPeriod(LocalDateTime start, LocalDateTime end) {
        return repairTicketRepository.findAll().stream()
                .filter(ticket -> ticket.getClosedAt() != null &&
                        DateUtils.isWithinRange(ticket.getClosedAt(), start, end))
                .toList()
                .size();
    }
}