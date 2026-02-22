package com.yann.phoenix_exchange_api.repository;

import com.yann.phoenix_exchange_api.entity.pricing.DiagnosticChecklist;
import com.yann.phoenix_exchange_api.entity.pricing.RiskLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DiagnosticChecklistRepository extends JpaRepository<DiagnosticChecklist, Long> {
    Optional<DiagnosticChecklist> findByProductId(Long productId);
    List<DiagnosticChecklist> findByTechnicianId(Long technicianId);
    List<DiagnosticChecklist> findByRiskLevel(RiskLevel riskLevel);
    List<DiagnosticChecklist> findByEstimationId(Long estimationId);
}
