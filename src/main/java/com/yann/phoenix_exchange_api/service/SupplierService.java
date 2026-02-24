package com.yann.phoenix_exchange_api.service;

import com.yann.phoenix_exchange_api.dto.supplier.SupplierCreateDTO;
import com.yann.phoenix_exchange_api.dto.supplier.SupplierDTO;
import com.yann.phoenix_exchange_api.dto.supplier.SupplierUpdateDTO;
import com.yann.phoenix_exchange_api.entity.purchase.Supplier;
import com.yann.phoenix_exchange_api.entity.purchase.SupplierType;
import com.yann.phoenix_exchange_api.exception.ResourceNotFoundException;
import com.yann.phoenix_exchange_api.mapper.SupplierMapper;
import com.yann.phoenix_exchange_api.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SupplierService {

    private final SupplierRepository supplierRepository;
    private final SupplierMapper supplierMapper;

    public SupplierDTO getById(Long id) {
        Supplier supplier = findSupplierById(id);
        return supplierMapper.toDTO(supplier);
    }

    public Page<SupplierDTO> getAll(Pageable pageable) {
        return supplierRepository.findAll(pageable)
                .map(supplierMapper::toDTO);
    }

    public List<SupplierDTO> getByType(SupplierType type) {
        return supplierRepository.findByType(type).stream()
                .map(supplierMapper::toDTO)
                .toList();
    }

    public List<SupplierDTO> getReliableSuppliers() {
        return supplierRepository.findByRatingGreaterThanEqual(new BigDecimal("4.0")).stream()
                .map(supplierMapper::toDTO)
                .toList();
    }

    public SupplierDTO create(SupplierCreateDTO createDTO) {
        Supplier supplier = supplierMapper.toEntity(createDTO);

        Supplier saved = supplierRepository.save(supplier);
        log.info("Supplier created: {}", saved.getName());

        return supplierMapper.toDTO(saved);
    }

    public SupplierDTO update(Long id, SupplierUpdateDTO updateDTO) {
        Supplier supplier = findSupplierById(id);
        supplierMapper.updateEntityFromDTO(updateDTO, supplier);

        Supplier updated = supplierRepository.save(supplier);
        log.info("Supplier updated: {}", id);

        return supplierMapper.toDTO(updated);
    }

    public void delete(Long id) {
        if (!supplierRepository.existsById(id)) {
            throw new ResourceNotFoundException("Supplier not found: " + id);
        }

        supplierRepository.deleteById(id);
        log.info("Supplier deleted: {}", id);
    }

    private Supplier findSupplierById(Long id) {
        return supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found: " + id));
    }
}