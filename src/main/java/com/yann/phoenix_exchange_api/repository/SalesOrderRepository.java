package com.yann.phoenix_exchange_api.repository;

import com.yann.phoenix_exchange_api.entity.sale.PaymentStatus;
import com.yann.phoenix_exchange_api.entity.sale.SalesOrder;
import com.yann.phoenix_exchange_api.entity.sale.SalesOrderStatus;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SalesOrderRepository extends JpaRepository<SalesOrder, Long> {
    Optional<SalesOrder> findByOrderNumber(String orderNumber);
    List<SalesOrder> findByStatus(SalesOrderStatus status);
    Page<SalesOrder> findByCustomerId(Long customerId, Pageable pageable);
    List<SalesOrder> findByPaymentStatus(PaymentStatus paymentStatus);
    List<SalesOrder> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT SUM(o.totalAmount) FROM SalesOrder o WHERE o.status = 'DELIVERED' AND o.createdAt BETWEEN :start AND :end")
    BigDecimal calculateRevenueBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    Long countByStatus(SalesOrderStatus status);
}
