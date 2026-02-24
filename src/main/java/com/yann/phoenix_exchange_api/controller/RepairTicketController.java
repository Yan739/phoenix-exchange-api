package com.yann.phoenix_exchange_api.controller;

import com.yann.phoenix_exchange_api.dto.repair.CloseTicketDTO;
import com.yann.phoenix_exchange_api.dto.repair.RepairTicketCreateDTO;
import com.yann.phoenix_exchange_api.dto.repair.RepairTicketDTO;
import com.yann.phoenix_exchange_api.entity.repair.TicketStatus;
import com.yann.phoenix_exchange_api.service.RepairTicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/repair-tickets")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class RepairTicketController {

    private final RepairTicketService repairTicketService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TECHNICIAN')")
    public ResponseEntity<Page<RepairTicketDTO>> getAll(Pageable pageable) {
        return ResponseEntity.ok(repairTicketService.getAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RepairTicketDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(repairTicketService.getById(id));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<RepairTicketDTO>> getByStatus(@PathVariable TicketStatus status) {
        return ResponseEntity.ok(repairTicketService.getByStatus(status));
    }

    @GetMapping("/technician/{technicianId}")
    public ResponseEntity<List<RepairTicketDTO>> getByTechnician(@PathVariable Long technicianId) {
        return ResponseEntity.ok(repairTicketService.getByTechnician(technicianId));
    }

    @GetMapping("/urgent-overdue")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<RepairTicketDTO>> getOverdueUrgentTickets() {
        return ResponseEntity.ok(repairTicketService.findOverdueUrgentTickets());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TECHNICIAN', 'COMMERCIAL')")
    public ResponseEntity<RepairTicketDTO> create(@Valid @RequestBody RepairTicketCreateDTO createDTO) {
        RepairTicketDTO created = repairTicketService.create(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PatchMapping("/{id}/assign")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<RepairTicketDTO> assignToTechnician(
            @PathVariable Long id,
            @RequestParam Long technicianId) {
        return ResponseEntity.ok(repairTicketService.assignToTechnician(id, technicianId));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TECHNICIAN')")
    public ResponseEntity<RepairTicketDTO> changeStatus(
            @PathVariable Long id,
            @RequestParam TicketStatus status) {
        return ResponseEntity.ok(repairTicketService.changeStatus(id, status));
    }

    @PostMapping("/{id}/close")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TECHNICIAN')")
    public ResponseEntity<RepairTicketDTO> close(
            @PathVariable Long id,
            @Valid @RequestBody CloseTicketDTO closeDTO) {
        return ResponseEntity.ok(repairTicketService.close(id, closeDTO));
    }
}