package com.yann.phoenix_exchange_api.repository;

import com.yann.phoenix_exchange_api.entity.repair.Intervention;
import com.yann.phoenix_exchange_api.entity.repair.InterventionStatus;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface InterventionRepository extends JpaRepository<Intervention, Long> {
    List<Intervention> findByTicketId(Long ticketId);
    List<Intervention> findByTechnicianId(Long technicianId);
    List<Intervention> findByStatus(InterventionStatus status);

    @Query("SELECT SUM(i.cost) FROM Intervention i WHERE i.ticket.id = :ticketId")
    BigDecimal sumCostByTicket(@Param("ticketId") Long ticketId);
}
