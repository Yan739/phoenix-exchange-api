package com.yann.phoenix_exchange_api.dto.dashboard;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplierStatsDTO {
    private Integer totalSuppliers;
    private Integer activeSuppliers;
    private Integer reliableSuppliers;

    private List<TopSupplierDTO> topSuppliers;

    private BigDecimal totalPurchaseVolume;
    private BigDecimal averagePurchaseVolume;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopSupplierDTO {
        private Long id;
        private String name;
        private String type;
        private BigDecimal totalPurchases;
        private Integer productsSupplied;
        private BigDecimal rating;
        private Boolean isReliable;
    }
}
