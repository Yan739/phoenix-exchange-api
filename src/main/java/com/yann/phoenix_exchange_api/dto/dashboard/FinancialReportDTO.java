package com.yann.phoenix_exchange_api.dto.dashboard;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinancialReportDTO {
    private String reportType;
    private LocalDate startDate;
    private LocalDate endDate;

    // Revenue
    private BigDecimal totalRevenue;
    private BigDecimal productRevenue;
    private BigDecimal serviceRevenue;

    // Costs
    private BigDecimal totalCosts;
    private BigDecimal purchaseCosts;
    private BigDecimal repairCosts;
    private BigDecimal operationalCosts;

    // Margins
    private BigDecimal grossProfit;
    private BigDecimal grossProfitMargin;
    private BigDecimal netProfit;
    private BigDecimal netProfitMargin;

    // Cash Flow
    private BigDecimal cashInflow;
    private BigDecimal cashOutflow;
    private BigDecimal netCashFlow;

    // Breakdown
    private RevenueBreakdownDTO revenueBreakdown;
    private CostBreakdownDTO costBreakdown;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RevenueBreakdownDTO {
        private BigDecimal smartphones;
        private BigDecimal laptops;
        private BigDecimal tablets;
        private BigDecimal accessories;
        private BigDecimal other;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CostBreakdownDTO {
        private BigDecimal purchases;
        private BigDecimal repairs;
        private BigDecimal labor;
        private BigDecimal overhead;
        private BigDecimal marketing;
        private BigDecimal other;
    }
}