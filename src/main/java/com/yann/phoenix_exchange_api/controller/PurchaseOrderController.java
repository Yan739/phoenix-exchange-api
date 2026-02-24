package com.yann.phoenix_exchange_api.controller;

import com.yann.phoenix_exchange_api.dto.purchase.PurchaseOrderCreateDTO;
import com.yann.phoenix_exchange_api.dto.purchase.PurchaseOrderDTO;
import com.yann.phoenix_exchange_api.entity.purchase.OrderStatus;
import com.yann.phoenix_exchange_api.service.PurchaseOrderService;
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
@RequestMapping("/api/purchase-orders")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'COMMERCIAL')")
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    @GetMapping
    public ResponseEntity<Page<PurchaseOrderDTO>> getAll(Pageable pageable) {
        return ResponseEntity.ok(purchaseOrderService.getAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PurchaseOrderDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(purchaseOrderService.getById(id));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<PurchaseOrderDTO>> getByStatus(@PathVariable OrderStatus status) {
        return ResponseEntity.ok(purchaseOrderService.getByStatus(status));
    }

    @GetMapping("/supplier/{supplierId}")
    public ResponseEntity<List<PurchaseOrderDTO>> getBySupplier(@PathVariable Long supplierId) {
        return ResponseEntity.ok(purchaseOrderService.getBySupplier(supplierId));
    }

    @PostMapping
    public ResponseEntity<PurchaseOrderDTO> create(@Valid @RequestBody PurchaseOrderCreateDTO createDTO) {
        PurchaseOrderDTO created = purchaseOrderService.create(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<PurchaseOrderDTO> approve(@PathVariable Long id) {
        return ResponseEntity.ok(purchaseOrderService.approve(id));
    }

    @PatchMapping("/{id}/receive")
    public ResponseEntity<PurchaseOrderDTO> receive(@PathVariable Long id) {
        return ResponseEntity.ok(purchaseOrderService.receive(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Void> cancel(@PathVariable Long id) {
        purchaseOrderService.cancel(id);
        return ResponseEntity.noContent().build();
    }
}