package com.yann.phoenix_exchange_api.controller;

import com.yann.phoenix_exchange_api.dto.supplier.SupplierCreateDTO;
import com.yann.phoenix_exchange_api.dto.supplier.SupplierDTO;
import com.yann.phoenix_exchange_api.dto.supplier.SupplierUpdateDTO;
import com.yann.phoenix_exchange_api.entity.purchase.SupplierType;
import com.yann.phoenix_exchange_api.service.SupplierService;
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
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'COMMERCIAL')")
public class SupplierController {

    private final SupplierService supplierService;

    @GetMapping
    public ResponseEntity<Page<SupplierDTO>> getAll(Pageable pageable) {
        return ResponseEntity.ok(supplierService.getAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SupplierDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(supplierService.getById(id));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<SupplierDTO>> getByType(@PathVariable SupplierType type) {
        return ResponseEntity.ok(supplierService.getByType(type));
    }

    @GetMapping("/reliable")
    public ResponseEntity<List<SupplierDTO>> getReliableSuppliers() {
        return ResponseEntity.ok(supplierService.getReliableSuppliers());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<SupplierDTO> create(@Valid @RequestBody SupplierCreateDTO createDTO) {
        SupplierDTO created = supplierService.create(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<SupplierDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody SupplierUpdateDTO updateDTO) {
        return ResponseEntity.ok(supplierService.update(id, updateDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        supplierService.delete(id);
        return ResponseEntity.noContent().build();
    }
}