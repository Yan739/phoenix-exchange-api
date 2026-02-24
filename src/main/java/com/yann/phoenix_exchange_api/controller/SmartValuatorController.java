package com.yann.phoenix_exchange_api.controller;

import com.yann.phoenix_exchange_api.dto.valuator.*;
import com.yann.phoenix_exchange_api.service.DiagnosticService;
import com.yann.phoenix_exchange_api.service.SmartValuatorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/valuator")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class SmartValuatorController {

    private final SmartValuatorService smartValuatorService;
    private final DiagnosticService diagnosticService;

    @PostMapping("/estimate")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'COMMERCIAL')")
    public ResponseEntity<EstimationResponseDTO> estimate(
            @Valid @RequestBody EstimationRequestDTO request) {
        EstimationResponseDTO response = smartValuatorService.estimate(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/diagnostic/checklist")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TECHNICIAN')")
    public ResponseEntity<DiagnosticChecklistDTO> createChecklist(
            @Valid @RequestBody DiagnosticChecklistCreateDTO createDTO) {
        DiagnosticChecklistDTO checklist = diagnosticService.createChecklist(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(checklist);
    }

    @GetMapping("/diagnostic/checklist/{id}")
    public ResponseEntity<DiagnosticChecklistDTO> getChecklist(@PathVariable Long id) {
        return ResponseEntity.ok(diagnosticService.getChecklistById(id));
    }

    @PostMapping("/calculate-purchase-price")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'COMMERCIAL')")
    public ResponseEntity<PurchasePriceCalculationDTO> calculatePurchasePrice(
            @RequestParam Long checklistId,
            @RequestParam Integer targetMarginPct) {
        PurchasePriceCalculationDTO calculation = diagnosticService.calculatePurchasePrice(
                checklistId,
                targetMarginPct
        );
        return ResponseEntity.ok(calculation);
    }
}