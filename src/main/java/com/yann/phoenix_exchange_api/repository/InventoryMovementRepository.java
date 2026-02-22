package com.yann.phoenix_exchange_api.repository;

import com.yann.phoenix_exchange_api.entity.inventory.InventoryMovement;
import com.yann.phoenix_exchange_api.entity.inventory.MovementType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InventoryMovementRepository extends JpaRepository<InventoryMovement, Long> {
    List<InventoryMovement> findByProductId(Long productId);
    List<InventoryMovement> findByWarehouseId(Long warehouseId);
    List<InventoryMovement> findByMovementType(MovementType type);
    List<InventoryMovement> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}
