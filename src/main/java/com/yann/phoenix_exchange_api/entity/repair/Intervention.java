package com.yann.phoenix_exchange_api.entity.repair;

import com.yann.phoenix_exchange_api.entity.user.User;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "interventions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Intervention {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    private RepairTicket ticket;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "technician_id")
    private User technician;

    @Column(name = "type", length = 100)
    private String type;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "parts_used", columnDefinition = "TEXT")
    private String partsUsed;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(name = "cost", precision = 10, scale = 2)
    private BigDecimal cost;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50)
    private InterventionStatus status = InterventionStatus.PENDING;

    @Column(name = "created_at", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Business methods
    public void start() {
        if (this.status != InterventionStatus.PENDING) {
            throw new IllegalStateException("Only PENDING interventions can be started");
        }
        this.status = InterventionStatus.IN_PROGRESS;
    }

    public void complete() {
        if (this.status != InterventionStatus.IN_PROGRESS) {
            throw new IllegalStateException("Only IN_PROGRESS interventions can be completed");
        }
        this.status = InterventionStatus.COMPLETED;
    }

    public boolean isCompleted() {
        return this.status == InterventionStatus.COMPLETED;
    }
}