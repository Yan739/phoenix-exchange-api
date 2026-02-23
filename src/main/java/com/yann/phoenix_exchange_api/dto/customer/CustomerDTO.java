package com.yann.phoenix_exchange_api.dto.customer;

import com.yann.phoenix_exchange_api.entity.sale.CustomerSegment;
import com.yann.phoenix_exchange_api.entity.sale.CustomerType;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDTO {
    private Long id;
    private CustomerType type;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String vatNumber;
    private Integer loyaltyPoints;
    private BigDecimal totalSpent;
    private CustomerSegment segment;
    private Boolean isActive;
    private LocalDateTime createdAt;

    // Calculated
    private Boolean isVIP;
    private BigDecimal discountRate;
    private Integer totalOrders;
    private LocalDateTime lastOrderDate;
}