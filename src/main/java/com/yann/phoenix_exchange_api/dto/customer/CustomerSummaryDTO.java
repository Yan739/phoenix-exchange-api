package com.yann.phoenix_exchange_api.dto.customer;

import com.yann.phoenix_exchange_api.entity.sale.CustomerSegment;
import com.yann.phoenix_exchange_api.entity.sale.CustomerType;
import lombok.*;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerSummaryDTO {
    private Long id;
    private String name;
    private CustomerType type;
    private CustomerSegment segment;
    private BigDecimal totalSpent;
    private Boolean isActive;
}
