package com.yann.phoenix_exchange_api.dto.dashboard;

import com.yann.phoenix_exchange_api.entity.repair.Priority;
import com.yann.phoenix_exchange_api.entity.repair.TicketStatus;
import lombok.*;
import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RepairStatsDTO {
    private Integer totalTickets;
    private Integer openTickets;
    private Integer closedTickets;

    // By Status
    private Map<TicketStatus, Integer> ticketsByStatus;

    // By Priority
    private Map<Priority, Integer> ticketsByPriority;

    // Performance
    private Double averageRepairDuration;
    private Double averageRepairCost;
    private BigDecimal totalRepairCosts;

    // Efficiency
    private Integer ticketsClosedThisMonth;
    private Double costVariancePercentage;
    private Double durationVariancePercentage;

    // Alerts
    private Integer overdueTickets;
    private Integer urgentUnassignedTickets;
}