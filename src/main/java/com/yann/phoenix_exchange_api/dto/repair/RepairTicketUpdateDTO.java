package com.yann.phoenix_exchange_api.dto.repair;

import com.yann.phoenix_exchange_api.entity.repair.Priority;
import com.yann.phoenix_exchange_api.entity.repair.TicketStatus;
import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RepairTicketUpdateDTO {
    private TicketStatus status;
    private Priority priority;
    private Long assignedToId;
    private BigDecimal estimatedCost;
    private Integer estimatedDurationHours;
    private String diagnosis;
}
