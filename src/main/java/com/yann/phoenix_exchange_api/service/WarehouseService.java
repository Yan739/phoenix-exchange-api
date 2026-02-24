package com.yann.phoenix_exchange_api.service;

import com.yann.phoenix_exchange_api.dto.warehouse.WarehouseCreateDTO;
import com.yann.phoenix_exchange_api.dto.warehouse.WarehouseDTO;
import com.yann.phoenix_exchange_api.dto.warehouse.WarehouseUpdateDTO;
import com.yann.phoenix_exchange_api.entity.inventory.Warehouse;
import com.yann.phoenix_exchange_api.entity.inventory.WarehouseType;
import com.yann.phoenix_exchange_api.exception.ResourceNotFoundException;
import com.yann.phoenix_exchange_api.mapper.WarehouseMapper;
import com.yann.phoenix_exchange_api.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final WarehouseMapper warehouseMapper;

    public WarehouseDTO getById(Long id) {
        Warehouse warehouse = findWarehouseById(id);
        return warehouseMapper.toDTO(warehouse);
    }

    public Page<WarehouseDTO> getAll(Pageable pageable) {
        return warehouseRepository.findAll(pageable)
                .map(warehouseMapper::toDTO);
    }

    public List<WarehouseDTO> getByType(WarehouseType type) {
        return warehouseRepository.findByType(type).stream()
                .map(warehouseMapper::toDTO)
                .toList();
    }

    public List<WarehouseDTO> getWarehousesWithCapacity() {
        return warehouseRepository.findWarehousesWithCapacity().stream()
                .map(warehouseMapper::toDTO)
                .toList();
    }

    public WarehouseDTO create(WarehouseCreateDTO createDTO) {
        Warehouse warehouse = warehouseMapper.toEntity(createDTO);

        Warehouse saved = warehouseRepository.save(warehouse);
        log.info("Warehouse created: {}", saved.getName());

        return warehouseMapper.toDTO(saved);
    }

    public WarehouseDTO update(Long id, WarehouseUpdateDTO updateDTO) {
        Warehouse warehouse = findWarehouseById(id);
        warehouseMapper.updateEntityFromDTO(updateDTO, warehouse);

        Warehouse updated = warehouseRepository.save(warehouse);
        log.info("Warehouse updated: {}", id);

        return warehouseMapper.toDTO(updated);
    }

    public void delete(Long id) {
        if (!warehouseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Warehouse not found: " + id);
        }

        warehouseRepository.deleteById(id);
        log.info("Warehouse deleted: {}", id);
    }

    private Warehouse findWarehouseById(Long id) {
        return warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found: " + id));
    }
}