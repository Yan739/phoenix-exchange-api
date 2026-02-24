package com.yann.phoenix_exchange_api.controller;

import com.yann.phoenix_exchange_api.dto.customer.CustomerCreateDTO;
import com.yann.phoenix_exchange_api.dto.customer.CustomerDTO;
import com.yann.phoenix_exchange_api.dto.customer.CustomerUpdateDTO;
import com.yann.phoenix_exchange_api.entity.sale.CustomerSegment;
import com.yann.phoenix_exchange_api.entity.sale.CustomerType;
import com.yann.phoenix_exchange_api.service.CustomerService;
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
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'COMMERCIAL')")
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    public ResponseEntity<Page<CustomerDTO>> getAll(Pageable pageable) {
        return ResponseEntity.ok(customerService.getAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.getById(id));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<CustomerDTO>> getByType(@PathVariable CustomerType type) {
        return ResponseEntity.ok(customerService.getByType(type));
    }

    @GetMapping("/segment/{segment}")
    public ResponseEntity<List<CustomerDTO>> getBySegment(@PathVariable CustomerSegment segment) {
        return ResponseEntity.ok(customerService.getBySegment(segment));
    }

    @GetMapping("/vip")
    public ResponseEntity<List<CustomerDTO>> getVIPCustomers() {
        return ResponseEntity.ok(customerService.getVIPCustomers());
    }

    @PostMapping
    public ResponseEntity<CustomerDTO> create(@Valid @RequestBody CustomerCreateDTO createDTO) {
        CustomerDTO created = customerService.create(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody CustomerUpdateDTO updateDTO) {
        return ResponseEntity.ok(customerService.update(id, updateDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        customerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}