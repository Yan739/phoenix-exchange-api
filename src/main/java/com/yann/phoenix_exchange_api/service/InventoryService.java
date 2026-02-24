package com.yann.phoenix_exchange_api.service;

import com.yann.phoenix_exchange_api.dto.warehouse.InventoryMovementCreateDTO;
import com.yann.phoenix_exchange_api.dto.warehouse.InventoryMovementDTO;
import com.yann.phoenix_exchange_api.entity.inventory.InventoryMovement;
import com.yann.phoenix_exchange_api.entity.inventory.MovementType;
import com.yann.phoenix_exchange_api.entity.inventory.Warehouse;
import com.yann.phoenix_exchange_api.entity.product.Product;
import com.yann.phoenix_exchange_api.entity.user.User;
import com.yann.phoenix_exchange_api.exception.InsufficientStockException;
import com.yann.phoenix_exchange_api.exception.ResourceNotFoundException;
import com.yann.phoenix_exchange_api.mapper.InventoryMovementMapper;
import com.yann.phoenix_exchange_api.repository.InventoryMovementRepository;
import com.yann.phoenix_exchange_api.repository.ProductRepository;
import com.yann.phoenix_exchange_api.repository.WarehouseRepository;
import com.yann.phoenix_exchange_api.security.SecurityUtils;
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
public class InventoryService {

    private final InventoryMovementRepository inventoryMovementRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    private final InventoryMovementMapper inventoryMovementMapper;
    private final SecurityUtils securityUtils;

    public Page<InventoryMovementDTO> getAll(Pageable pageable) {
        return inventoryMovementRepository.findAll(pageable)
                .map(inventoryMovementMapper::toDTO);
    }

    public List<InventoryMovementDTO> getByProduct(Long productId) {
        return inventoryMovementRepository.findByProductId(productId).stream()
                .map(inventoryMovementMapper::toDTO)
                .toList();
    }

    public List<InventoryMovementDTO> getByWarehouse(Long warehouseId) {
        return inventoryMovementRepository.findByWarehouseId(warehouseId).stream()
                .map(inventoryMovementMapper::toDTO)
                .toList();
    }

    public InventoryMovementDTO createMovement(InventoryMovementCreateDTO createDTO) {
        Product product = productRepository.findById(createDTO.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        Warehouse warehouse = null;
        if (createDTO.getWarehouseId() != null) {
            warehouse = warehouseRepository.findById(createDTO.getWarehouseId())
                    .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found"));
        }

        User currentUser = securityUtils.getCurrentUser().orElse(null);

        // Verify warehouse capacity for IN movements
        if (createDTO.getMovementType() == MovementType.IN && warehouse != null) {
            if (!warehouse.canAddStock(createDTO.getQuantity())) {
                throw new InsufficientStockException("Warehouse is full or does not have enough capacity");
            }
        }

        // Create movement
        InventoryMovement movement = inventoryMovementMapper.toEntity(createDTO);
        movement.setProduct(product);
        movement.setWarehouse(warehouse);
        movement.setCreatedBy(currentUser);

        // Execute movement (update warehouse stock)
        movement.execute();

        // Update product warehouse for IN movements
        if (createDTO.getMovementType() == MovementType.IN && warehouse != null) {
            product.setWarehouse(warehouse);
            productRepository.save(product);
        }

        InventoryMovement saved = inventoryMovementRepository.save(movement);

        // Save warehouse changes
        if (warehouse != null) {
            warehouseRepository.save(warehouse);
        }

        log.info("Inventory movement created: {} for product {}",
                createDTO.getMovementType(), product.getSerialNumber());

        return inventoryMovementMapper.toDTO(saved);
    }

    public void transferProduct(Long productId, Long fromWarehouseId, Long toWarehouseId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        Warehouse fromWarehouse = warehouseRepository.findById(fromWarehouseId)
                .orElseThrow(() -> new ResourceNotFoundException("From warehouse not found"));

        Warehouse toWarehouse = warehouseRepository.findById(toWarehouseId)
                .orElseThrow(() -> new ResourceNotFoundException("To warehouse not found"));

        // Check capacity
        if (!toWarehouse.canAddStock(1)) {
            throw new InsufficientStockException("Destination warehouse is full");
        }

        // Create OUT movement from source
        InventoryMovement outMovement = InventoryMovement.builder()
                .product(product)
                .warehouse(fromWarehouse)
                .movementType(MovementType.OUT)
                .quantity(1)
                .referenceType("TRANSFER")
                .createdBy(securityUtils.getCurrentUser().orElse(null))
                .build();

        outMovement.execute();
        inventoryMovementRepository.save(outMovement);
        warehouseRepository.save(fromWarehouse);

        // Create IN movement to destination
        InventoryMovement inMovement = InventoryMovement.builder()
                .product(product)
                .warehouse(toWarehouse)
                .movementType(MovementType.IN)
                .quantity(1)
                .referenceType("TRANSFER")
                .createdBy(securityUtils.getCurrentUser().orElse(null))
                .build();

        inMovement.execute();
        inventoryMovementRepository.save(inMovement);

        // Update product warehouse
        product.setWarehouse(toWarehouse);
        productRepository.save(product);

        warehouseRepository.save(toWarehouse);

        log.info("Product {} transferred from warehouse {} to {}",
                productId, fromWarehouseId, toWarehouseId);
    }
}