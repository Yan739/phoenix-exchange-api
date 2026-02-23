package com.yann.phoenix_exchange_api.dto.dashboard;

import com.yann.phoenix_exchange_api.entity.sale.CustomerSegment;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerStatsDTO {
    private Integer totalCustomers;
    private Integer activeCustomers;

    // By Segment
    private Map<CustomerSegment, Integer> customersBySegment;

    // Top Customers
    private List<TopCustomerDTO> topCustomers; // Top 10 by spending

    // Metrics
    private BigDecimal averageCustomerValue;
    private BigDecimal customerLifetimeValue;
    private Integer newCustomersThisMonth;
    private Integer returningCustomersThisMonth;

    // Retention
    private Double retentionRate;
    private Double churnRate;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopCustomerDTO {
        private Long id;
        private String name;
        private String type;
        private CustomerSegment segment;
        private BigDecimal totalSpent;
        private Integer totalOrders;
        private Integer loyaltyPoints;
    }
}