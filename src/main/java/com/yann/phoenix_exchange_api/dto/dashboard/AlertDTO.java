package com.yann.phoenix_exchange_api.dto.dashboard;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertDTO {
    private Long id;
    private String type;
    private String severity;
    private String title;
    private String message;
    private String actionUrl;
    private String actionLabel;

    // Context
    private Long referenceId;
    private String referenceType;

    // Timestamp
    private LocalDateTime createdAt;
    private Boolean isRead;
    private Boolean isDismissed;
}