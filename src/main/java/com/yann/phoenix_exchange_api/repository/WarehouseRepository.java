package com.yann.phoenix_exchange_api.repository;

import com.yann.phoenix_exchange_api.entity.inventory.Warehouse;
import com.yann.phoenix_exchange_api.entity.inventory.WarehouseType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {
    List<Warehouse> findByType(WarehouseType type);
    List<Warehouse> findByIsActive(Boolean isActive);

    @Query("SELECT w FROM Warehouse w WHERE w.currentStock < w.capacity * 0.8")
    List<Warehouse> findWarehousesWithCapacity();
}
