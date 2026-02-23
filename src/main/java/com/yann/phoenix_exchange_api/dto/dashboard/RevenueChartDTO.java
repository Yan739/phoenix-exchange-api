package com.yann.phoenix_exchange_api.dto.dashboard;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RevenueChartDTO {
    private String period;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<DataPointDTO> dataPoints;
    private BigDecimal totalRevenue;
    private BigDecimal averageRevenue;
    private BigDecimal peakRevenue;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DataPointDTO {
        private LocalDate date;
        private String label;
        private BigDecimal revenue;
        private Integer ordersCount;
        private BigDecimal averageOrderValue;
    }
}