package com.yann.phoenix_exchange_api.repository;

import com.yann.phoenix_exchange_api.entity.repair.Priority;
import com.yann.phoenix_exchange_api.entity.repair.RepairTicket;
import com.yann.phoenix_exchange_api.entity.repair.TicketStatus;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RepairTicketRepository extends JpaRepository<RepairTicket, Long> {
    Optional<RepairTicket> findByTicketNumber(String ticketNumber);
    List<RepairTicket> findByStatus(TicketStatus status);
    Page<RepairTicket> findByStatus(TicketStatus status, Pageable pageable);
    List<RepairTicket> findByPriority(Priority priority);
    List<RepairTicket> findByAssignedToId(Long technicianId);
    List<RepairTicket> findByProductId(Long productId);

    @Query("SELECT t FROM RepairTicket t WHERE t.status = :status AND t.assignedTo IS NULL")
    List<RepairTicket> findUnassignedByStatus(@Param("status") TicketStatus status);

    @Query("SELECT t FROM RepairTicket t WHERE t.priority = 'URGENT' AND t.assignedTo IS NULL AND t.createdAt < :thresholdTime")
    List<RepairTicket> findOverdueUrgentTickets(@Param("thresholdTime") LocalDateTime thresholdTime);

    Long countByStatus(TicketStatus status);
}
