package com.yann.phoenix_exchange_api.dto.valuator;

import com.yann.phoenix_exchange_api.entity.product.Grade;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstimationResponseDTO {
    private Long id;
    private BigDecimal estimatedPrice;
    private String aiDescription;
    private Integer confidence;
    private Grade suggestedGrade;
    private Boolean isFallback;
    private LocalDateTime createdAt;
}
