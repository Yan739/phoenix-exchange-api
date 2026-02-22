package com.yann.phoenix_exchange_api.entity.repair;

import com.yann.phoenix_exchange_api.entity.product.Grade;
import com.yann.phoenix_exchange_api.entity.product.Product;
import com.yann.phoenix_exchange_api.entity.product.ProductStatus;
import com.yann.phoenix_exchange_api.entity.user.User;
import com.yann.phoenix_exchange_api.entity.user.UserRole;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "repair_tickets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepairTicket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ticket_number", unique = true, nullable = false, length = 100)
    private String ticketNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to")
    private User assignedTo;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50)
    private TicketStatus status = TicketStatus.RECEIVED;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", length = 20)
    private Priority priority = Priority.NORMAL;

    @Column(name = "estimated_cost", precision = 10, scale = 2)
    private BigDecimal estimatedCost;

    @Column(name = "actual_cost", precision = 10, scale = 2)
    private BigDecimal actualCost;

    @Column(name = "estimated_duration_hours")
    private Integer estimatedDurationHours;

    @Column(name = "actual_duration_hours")
    private Integer actualDurationHours;

    @Column(name = "diagnosis", columnDefinition = "TEXT")
    private String diagnosis;

    @Column(name = "created_at", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;

    @Column(name = "closed_at")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime closedAt;

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Intervention> interventions = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Business methods
    public void assignToTechnician(User technician) {
        if (technician.getRole() != UserRole.TECHNICIAN) {
            throw new IllegalArgumentException("User must be a TECHNICIAN");
        }
        this.assignedTo = technician;
        if (this.status == TicketStatus.RECEIVED) {
            this.status = TicketStatus.DIAGNOSTIC;
        }
    }

    public void addIntervention(Intervention intervention) {
        intervention.setTicket(this);
        this.interventions.add(intervention);
    }

    public void close(Grade grade) {
        // Verify if all interventions are COMPLETED before closing ticket
        boolean allCompleted = interventions.stream()
                .allMatch(i -> i.getStatus() == InterventionStatus.COMPLETED);

        if (!allCompleted) {
            throw new IllegalStateException("All interventions must be COMPLETED before closing ticket");
        }

        this.status = TicketStatus.CLOSED;
        this.closedAt = LocalDateTime.now();

        // Real price calculation
        this.actualCost = calculateTotalCost();

        // update product status and grade
        this.product.setStatus(ProductStatus.REFURBISHED);
        this.product.assignGrade(grade);
    }

    public BigDecimal calculateTotalCost() {
        return interventions.stream()
                .map(Intervention::getCost)
                .filter(cost -> cost != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public boolean isOverdue() {
        if (closedAt != null) return false;
        if (estimatedDurationHours == null) return false;

        LocalDateTime deadline = createdAt.plusHours(estimatedDurationHours);
        return LocalDateTime.now().isAfter(deadline);
    }

    public boolean requiresUrgentAttention() {
        if (this.priority == Priority.URGENT && this.assignedTo == null) {
            long hoursUnassigned = java.time.Duration.between(createdAt, LocalDateTime.now()).toHours();
            return hoursUnassigned > 2;
        }
        return false;
    }

    public void changeStatus(TicketStatus newStatus) {
        if (!isTransitionAllowed(this.status, newStatus)) {
            throw new IllegalStateException(
                    String.format("Transition from %s to %s is not allowed", this.status, newStatus)
            );
        }
        this.status = newStatus;
    }

    private boolean isTransitionAllowed(TicketStatus from, TicketStatus to) {
        return switch (from) {
            case RECEIVED -> to == TicketStatus.DIAGNOSTIC;
            case DIAGNOSTIC -> to == TicketStatus.IN_REPAIR;
            case IN_REPAIR -> to == TicketStatus.TESTING || to == TicketStatus.IN_REPAIR;
            case TESTING -> to == TicketStatus.CLOSED || to == TicketStatus.IN_REPAIR;
            case CLOSED -> false;
        };
    }
}