package com.yann.phoenix_exchange_api.controller;

import com.yann.phoenix_exchange_api.dto.warehouse.WarehouseCreateDTO;
import com.yann.phoenix_exchange_api.dto.warehouse.WarehouseDTO;
import com.yann.phoenix_exchange_api.dto.warehouse.WarehouseUpdateDTO;
import com.yann.phoenix_exchange_api.entity.inventory.WarehouseType;
import com.yann.phoenix_exchange_api.service.WarehouseService;
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
@RequestMapping("/api/warehouses")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
public class WarehouseController {

    private final WarehouseService warehouseService;

    @GetMapping
    public ResponseEntity<Page<WarehouseDTO>> getAll(Pageable pageable) {
        return ResponseEntity.ok(warehouseService.getAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<WarehouseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(warehouseService.getById(id));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<WarehouseDTO>> getByType(@PathVariable WarehouseType type) {
        return ResponseEntity.ok(warehouseService.getByType(type));
    }

    @GetMapping("/available")
    public ResponseEntity<List<WarehouseDTO>> getWarehousesWithCapacity() {
        return ResponseEntity.ok(warehouseService.getWarehousesWithCapacity());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WarehouseDTO> create(@Valid @RequestBody WarehouseCreateDTO createDTO) {
        WarehouseDTO created = warehouseService.create(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WarehouseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody WarehouseUpdateDTO updateDTO) {
        return ResponseEntity.ok(warehouseService.update(id, updateDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        warehouseService.delete(id);
        return ResponseEntity.noContent().build();
    }
}