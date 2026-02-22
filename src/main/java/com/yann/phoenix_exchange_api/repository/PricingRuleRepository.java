package com.yann.phoenix_exchange_api.repository;

import com.yann.phoenix_exchange_api.entity.pricing.PricingRule;
import com.yann.phoenix_exchange_api.entity.product.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PricingRuleRepository extends JpaRepository<PricingRule, Long> {
    Optional<PricingRule> findByCategoryAndGrade(String category, Grade grade);
    List<PricingRule> findByCategory(String category);
    List<PricingRule> findByGrade(Grade grade);
    List<PricingRule> findByIsActive(Boolean isActive);
}
