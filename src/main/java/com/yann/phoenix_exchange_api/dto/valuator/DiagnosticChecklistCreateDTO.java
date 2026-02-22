package com.yann.phoenix_exchange_api.dto.valuator;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiagnosticChecklistCreateDTO {

    @NotNull
    private Long estimationId;

    private Long productId;

    @NotNull
    private Long technicianId;

    @NotNull
    private Map<String, Boolean> blockers;

    @NotNull
    private Map<String, Object> hardwareTests;
}
