package com.yann.phoenix_exchange_api.controller;

import com.yann.phoenix_exchange_api.dto.warehouse.InventoryMovementCreateDTO;
import com.yann.phoenix_exchange_api.dto.warehouse.InventoryMovementDTO;
import com.yann.phoenix_exchange_api.service.InventoryService;
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
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/movements")
    public ResponseEntity<Page<InventoryMovementDTO>> getAllMovements(Pageable pageable) {
        return ResponseEntity.ok(inventoryService.getAll(pageable));
    }

    @GetMapping("/movements/product/{productId}")
    public ResponseEntity<List<InventoryMovementDTO>> getByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(inventoryService.getByProduct(productId));
    }

    @GetMapping("/movements/warehouse/{warehouseId}")
    public ResponseEntity<List<InventoryMovementDTO>> getByWarehouse(@PathVariable Long warehouseId) {
        return ResponseEntity.ok(inventoryService.getByWarehouse(warehouseId));
    }

    @PostMapping("/movements")
    public ResponseEntity<InventoryMovementDTO> createMovement(
            @Valid @RequestBody InventoryMovementCreateDTO createDTO) {
        InventoryMovementDTO created = inventoryService.createMovement(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/transfer")
    public ResponseEntity<Void> transferProduct(
            @RequestParam Long productId,
            @RequestParam Long fromWarehouseId,
            @RequestParam Long toWarehouseId) {
        inventoryService.transferProduct(productId, fromWarehouseId, toWarehouseId);
        return ResponseEntity.ok().build();
    }
}