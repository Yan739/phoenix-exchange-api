package com.yann.phoenix_exchange_api.repository;

import com.yann.phoenix_exchange_api.entity.sale.Payment;
import com.yann.phoenix_exchange_api.entity.sale.PaymentMethod;
import com.yann.phoenix_exchange_api.entity.sale.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByOrderId(Long orderId);
    List<Payment> findByStatus(PaymentStatus status);
    List<Payment> findByMethod(PaymentMethod method);
    List<Payment> findByPaidAtBetween(LocalDateTime start, LocalDateTime end);
}