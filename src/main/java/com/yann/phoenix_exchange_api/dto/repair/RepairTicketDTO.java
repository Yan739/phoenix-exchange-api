package com.yann.phoenix_exchange_api.dto.repair;

import com.yann.phoenix_exchange_api.entity.repair.Priority;
import com.yann.phoenix_exchange_api.entity.repair.TicketStatus;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RepairTicketDTO {
    private Long id;
    private String ticketNumber;
    private TicketStatus status;
    private Priority priority;

    // Product
    private Long productId;
    private String productName;
    private String productSerialNumber;

    // Technician
    private Long assignedToId;
    private String assignedToName;

    // Costs & Duration
    private BigDecimal estimatedCost;
    private BigDecimal actualCost;
    private Integer estimatedDurationHours;
    private Integer actualDurationHours;
    private BigDecimal costVariance;

    // Details
    private String diagnosis;

    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime closedAt;

    // Interventions
    private List<InterventionDTO> interventions;
    private Integer interventionsCount;

    // Calculated
    private Boolean overdue;
    private Boolean requiresUrgentAttention;
    private Integer durationDays;
}

