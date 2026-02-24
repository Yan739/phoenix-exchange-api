package com.yann.phoenix_exchange_api.controller;

import com.yann.phoenix_exchange_api.dto.sale.SalesOrderCreateDTO;
import com.yann.phoenix_exchange_api.dto.sale.SalesOrderDTO;
import com.yann.phoenix_exchange_api.entity.sale.SalesOrderStatus;
import com.yann.phoenix_exchange_api.service.SalesOrderService;
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
@RequestMapping("/api/sales-orders")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'COMMERCIAL')")
public class SalesOrderController {

    private final SalesOrderService salesOrderService;

    @GetMapping
    public ResponseEntity<Page<SalesOrderDTO>> getAll(Pageable pageable) {
        return ResponseEntity.ok(salesOrderService.getAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SalesOrderDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(salesOrderService.getById(id));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<Page<SalesOrderDTO>> getByCustomer(
            @PathVariable Long customerId,
            Pageable pageable) {
        return ResponseEntity.ok(salesOrderService.getByCustomer(customerId, pageable));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<SalesOrderDTO>> getByStatus(@PathVariable SalesOrderStatus status) {
        return ResponseEntity.ok(salesOrderService.getByStatus(status));
    }

    @PostMapping
    public ResponseEntity<SalesOrderDTO> create(@Valid @RequestBody SalesOrderCreateDTO createDTO) {
        SalesOrderDTO created = salesOrderService.create(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/{id}/confirm-payment")
    public ResponseEntity<Void> confirmPayment(
            @PathVariable Long id,
            @RequestParam String transactionId) {
        salesOrderService.confirmPayment(id, transactionId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/ship")
    public ResponseEntity<Void> ship(@PathVariable Long id) {
        salesOrderService.ship(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/deliver")
    public ResponseEntity<Void> deliver(@PathVariable Long id) {
        salesOrderService.deliver(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancel(@PathVariable Long id) {
        salesOrderService.cancel(id);
        return ResponseEntity.noContent().build();
    }
}