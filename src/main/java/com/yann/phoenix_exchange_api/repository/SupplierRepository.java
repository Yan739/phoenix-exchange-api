package com.yann.phoenix_exchange_api.repository;

import com.yann.phoenix_exchange_api.entity.purchase.Supplier;
import com.yann.phoenix_exchange_api.entity.purchase.SupplierType;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    List<Supplier> findByType(SupplierType type);
    List<Supplier> findByIsActive(Boolean isActive);
    List<Supplier> findByRatingGreaterThanEqual(BigDecimal rating);

    @Query("SELECT s FROM Supplier s WHERE s.totalPurchases > :minAmount ORDER BY s.totalPurchases DESC")
    List<Supplier> findTopSuppliers(@Param("minAmount") BigDecimal minAmount);
}
