package com.yann.phoenix_exchange_api.mapper;

import com.yann.phoenix_exchange_api.dto.repair.InterventionCreateDTO;
import com.yann.phoenix_exchange_api.dto.repair.InterventionDTO;
import com.yann.phoenix_exchange_api.entity.repair.Intervention;
import org.springframework.stereotype.Component;

@Component
public class InterventionMapper {

    public InterventionDTO toDTO(Intervention entity) {
        if (entity == null) return null;

        return InterventionDTO.builder()
                .id(entity.getId())
                .ticketId(entity.getTicket().getId())
                .technicianId(entity.getTechnician() != null ? entity.getTechnician().getId() : null)
                .technicianName(entity.getTechnician() != null ? entity.getTechnician().getFullName() : null)
                .type(entity.getType())
                .description(entity.getDescription())
                .partsUsed(entity.getPartsUsed())
                .durationMinutes(entity.getDurationMinutes())
                .cost(entity.getCost())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public Intervention toEntity(InterventionCreateDTO dto) {
        if (dto == null) return null;

        return Intervention.builder()
                .type(dto.getType())
                .description(dto.getDescription())
                .partsUsed(dto.getPartsUsed())
                .durationMinutes(dto.getDurationMinutes())
                .cost(dto.getCost())
                .build();
    }
}