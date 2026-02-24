package com.yann.phoenix_exchange_api.mapper;

import com.yann.phoenix_exchange_api.dto.repair.RepairTicketCreateDTO;
import com.yann.phoenix_exchange_api.dto.repair.RepairTicketDTO;
import com.yann.phoenix_exchange_api.dto.repair.RepairTicketSummaryDTO;
import com.yann.phoenix_exchange_api.dto.repair.RepairTicketUpdateDTO;
import com.yann.phoenix_exchange_api.entity.repair.RepairTicket;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RepairTicketMapper {

    private final InterventionMapper interventionMapper;

    public RepairTicketDTO toDTO(RepairTicket entity) {
        if (entity == null) return null;

        return RepairTicketDTO.builder()
                .id(entity.getId())
                .ticketNumber(entity.getTicketNumber())
                .status(entity.getStatus())
                .priority(entity.getPriority())
                .productId(entity.getProduct().getId())
                .productName(entity.getProduct().getName())
                .productSerialNumber(entity.getProduct().getSerialNumber())
                .assignedToId(entity.getAssignedTo() != null ? entity.getAssignedTo().getId() : null)
                .assignedToName(entity.getAssignedTo() != null ? entity.getAssignedTo().getFullName() : null)
                .estimatedCost(entity.getEstimatedCost())
                .actualCost(entity.getActualCost())
                .estimatedDurationHours(entity.getEstimatedDurationHours())
                .actualDurationHours(entity.getActualDurationHours())
                .costVariance(calculateCostVariance(entity))
                .diagnosis(entity.getDiagnosis())
                .createdAt(entity.getCreatedAt())
                .closedAt(entity.getClosedAt())
                .interventions(entity.getInterventions().stream()
                        .map(interventionMapper::toDTO)
                        .collect(Collectors.toList()))
                .interventionsCount(entity.getInterventions().size())
                .overdue(entity.isOverdue())
                .requiresUrgentAttention(entity.requiresUrgentAttention())
                .durationDays(calculateDurationDays(entity))
                .build();
    }

    public RepairTicket toEntity(RepairTicketCreateDTO dto) {
        if (dto == null) return null;

        return RepairTicket.builder()
                .priority(dto.getPriority())
                .estimatedCost(dto.getEstimatedCost())
                .estimatedDurationHours(dto.getEstimatedDurationHours())
                .diagnosis(dto.getDiagnosis())
                .build();
    }

    public RepairTicketSummaryDTO toSummaryDTO(RepairTicket entity) {
        if (entity == null) return null;

        return RepairTicketSummaryDTO.builder()
                .id(entity.getId())
                .ticketNumber(entity.getTicketNumber())
                .productName(entity.getProduct().getName())
                .status(entity.getStatus())
                .priority(entity.getPriority())
                .assignedToName(entity.getAssignedTo() != null ? entity.getAssignedTo().getFullName() : null)
                .createdAt(entity.getCreatedAt())
                .overdue(entity.isOverdue())
                .build();
    }

    public void updateEntityFromDTO(RepairTicketUpdateDTO dto, RepairTicket entity) {
        if (dto == null || entity == null) return;

        if (dto.getStatus() != null) {
            entity.changeStatus(dto.getStatus());
        }
        if (dto.getPriority() != null) {
            entity.setPriority(dto.getPriority());
        }
        if (dto.getEstimatedCost() != null) {
            entity.setEstimatedCost(dto.getEstimatedCost());
        }
        if (dto.getEstimatedDurationHours() != null) {
            entity.setEstimatedDurationHours(dto.getEstimatedDurationHours());
        }
        if (dto.getDiagnosis() != null) {
            entity.setDiagnosis(dto.getDiagnosis());
        }
    }

    private BigDecimal calculateCostVariance(RepairTicket entity) {
        if (entity.getEstimatedCost() == null || entity.getActualCost() == null) {
            return null;
        }
        return entity.getActualCost().subtract(entity.getEstimatedCost());
    }

    private Integer calculateDurationDays(RepairTicket entity) {
        if (entity.getCreatedAt() == null) return null;
        LocalDateTime end = entity.getClosedAt() != null ? entity.getClosedAt() : LocalDateTime.now();
        return (int) ChronoUnit.DAYS.between(entity.getCreatedAt(), end);
    }
}