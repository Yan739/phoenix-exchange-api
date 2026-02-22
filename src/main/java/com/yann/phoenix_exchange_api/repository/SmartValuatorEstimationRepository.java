package com.yann.phoenix_exchange_api.repository;

import com.yann.phoenix_exchange_api.entity.pricing.SmartValuatorEstimation;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SmartValuatorEstimationRepository extends JpaRepository<SmartValuatorEstimation, Long> {
    List<SmartValuatorEstimation> findByBrand(String brand);
    List<SmartValuatorEstimation> findByCategory(String category);
    List<SmartValuatorEstimation> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT AVG(e.estimatedPrice) FROM SmartValuatorEstimation e WHERE e.category = :category")
    BigDecimal averageEstimationByCategory(@Param("category") String category);
}
