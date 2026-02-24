package com.yann.phoenix_exchange_api.controller;

import com.yann.phoenix_exchange_api.dto.product.ProductCreateDTO;
import com.yann.phoenix_exchange_api.dto.product.ProductDTO;
import com.yann.phoenix_exchange_api.dto.product.ProductFilterDTO;
import com.yann.phoenix_exchange_api.dto.product.ProductUpdateDTO;
import com.yann.phoenix_exchange_api.entity.product.Grade;
import com.yann.phoenix_exchange_api.entity.product.ProductStatus;
import com.yann.phoenix_exchange_api.service.ProductService;
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
@RequestMapping("/api/products")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'COMMERCIAL', 'TECHNICIAN')")
    public ResponseEntity<Page<ProductDTO>> getAll(Pageable pageable) {
        return ResponseEntity.ok(productService.getAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getById(id));
    }

    @GetMapping("/serial/{serialNumber}")
    public ResponseEntity<ProductDTO> getBySerialNumber(@PathVariable String serialNumber) {
        return ResponseEntity.ok(productService.getBySerialNumber(serialNumber));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<ProductDTO>> getByStatus(@PathVariable ProductStatus status) {
        return ResponseEntity.ok(productService.getByStatus(status));
    }

    @PostMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'COMMERCIAL')")
    public ResponseEntity<Page<ProductDTO>> search(
            @RequestBody ProductFilterDTO filters,
            Pageable pageable) {
        return ResponseEntity.ok(productService.getByFilters(filters, pageable));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'COMMERCIAL')")
    public ResponseEntity<ProductDTO> create(@Valid @RequestBody ProductCreateDTO createDTO) {
        ProductDTO created = productService.create(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ProductDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody ProductUpdateDTO updateDTO) {
        return ResponseEntity.ok(productService.update(id, updateDTO));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TECHNICIAN')")
    public ResponseEntity<Void> changeStatus(
            @PathVariable Long id,
            @RequestParam ProductStatus status) {
        productService.changeStatus(id, status);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/grade")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TECHNICIAN')")
    public ResponseEntity<Void> assignGrade(
            @PathVariable Long id,
            @RequestParam Grade grade) {
        productService.assignGrade(id, grade);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}