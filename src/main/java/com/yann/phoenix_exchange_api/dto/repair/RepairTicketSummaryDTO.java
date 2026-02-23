package com.yann.phoenix_exchange_api.dto.repair;

import com.yann.phoenix_exchange_api.entity.repair.Priority;
import com.yann.phoenix_exchange_api.entity.repair.TicketStatus;
import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RepairTicketSummaryDTO {
    private Long id;
    private String ticketNumber;
    private String productName;
    private TicketStatus status;
    private Priority priority;
    private String assignedToName;
    private LocalDateTime createdAt;
    private Boolean overdue;
}
