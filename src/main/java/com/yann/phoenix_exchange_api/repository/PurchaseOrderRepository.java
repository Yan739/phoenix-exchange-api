package com.yann.phoenix_exchange_api.repository;

import com.yann.phoenix_exchange_api.entity.purchase.OrderStatus;
import com.yann.phoenix_exchange_api.entity.purchase.PurchaseOrder;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {
    Optional<PurchaseOrder> findByOrderNumber(String orderNumber);
    List<PurchaseOrder> findByStatus(OrderStatus status);
    List<PurchaseOrder> findBySupplierId(Long supplierId);
    List<PurchaseOrder> findByCreatedById(Long userId);

    @Query("SELECT p FROM PurchaseOrder p WHERE p.status = 'PENDING' AND p.totalAmount > :threshold")
    List<PurchaseOrder> findPendingHighValueOrders(@Param("threshold") BigDecimal threshold);
}
