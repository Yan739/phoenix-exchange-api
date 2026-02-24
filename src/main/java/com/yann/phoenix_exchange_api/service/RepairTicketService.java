package com.yann.phoenix_exchange_api.service;

import com.yann.phoenix_exchange_api.dto.repair.CloseTicketDTO;
import com.yann.phoenix_exchange_api.dto.repair.RepairTicketCreateDTO;
import com.yann.phoenix_exchange_api.dto.repair.RepairTicketDTO;
import com.yann.phoenix_exchange_api.entity.product.Product;
import com.yann.phoenix_exchange_api.entity.product.ProductStatus;
import com.yann.phoenix_exchange_api.entity.repair.InterventionStatus;
import com.yann.phoenix_exchange_api.entity.repair.RepairTicket;
import com.yann.phoenix_exchange_api.entity.repair.TicketStatus;
import com.yann.phoenix_exchange_api.entity.user.User;
import com.yann.phoenix_exchange_api.exception.ResourceNotFoundException;
import com.yann.phoenix_exchange_api.mapper.RepairTicketMapper;
import com.yann.phoenix_exchange_api.repository.InterventionRepository;
import com.yann.phoenix_exchange_api.repository.ProductRepository;
import com.yann.phoenix_exchange_api.repository.RepairTicketRepository;
import com.yann.phoenix_exchange_api.repository.UserRepository;
import com.yann.phoenix_exchange_api.utils.NumberGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RepairTicketService {

    private final RepairTicketRepository repairTicketRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final InterventionRepository interventionRepository;
    private final RepairTicketMapper repairTicketMapper;

    public RepairTicketDTO getById(Long id) {
        RepairTicket ticket = findTicketById(id);
        return repairTicketMapper.toDTO(ticket);
    }

    public Page<RepairTicketDTO> getAll(Pageable pageable) {
        return repairTicketRepository.findAll(pageable)
                .map(repairTicketMapper::toDTO);
    }

    public List<RepairTicketDTO> getByStatus(TicketStatus status) {
        return repairTicketRepository.findByStatus(status).stream()
                .map(repairTicketMapper::toDTO)
                .toList();
    }

    public List<RepairTicketDTO> getByTechnician(Long technicianId) {
        return repairTicketRepository.findByAssignedToId(technicianId).stream()
                .map(repairTicketMapper::toDTO)
                .toList();
    }

    public RepairTicketDTO create(RepairTicketCreateDTO createDTO) {
        // Verify product exists and is in RECEIVED status
        Product product = productRepository.findById(createDTO.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (product.getStatus() != ProductStatus.RECEIVED) {
            throw new IllegalStateException("Product must be in RECEIVED status. Current: " + product.getStatus());
        }

        // Create ticket
        RepairTicket ticket = repairTicketMapper.toEntity(createDTO);
        ticket.setProduct(product);
        ticket.setTicketNumber(NumberGenerator.generateTicketNumber());
        ticket.setStatus(TicketStatus.RECEIVED);

        // Change product status
        product.changeStatus(ProductStatus.IN_REPAIR);
        productRepository.save(product);

        RepairTicket saved = repairTicketRepository.save(ticket);
        log.info("Repair ticket created: {}", saved.getTicketNumber());

        return repairTicketMapper.toDTO(saved);
    }

    public RepairTicketDTO assignToTechnician(Long ticketId, Long technicianId) {
        RepairTicket ticket = findTicketById(ticketId);
        User technician = userRepository.findById(technicianId)
                .orElseThrow(() -> new ResourceNotFoundException("Technician not found"));

        ticket.assignToTechnician(technician);

        RepairTicket updated = repairTicketRepository.save(ticket);
        log.info("Ticket {} assigned to technician {}", ticketId, technicianId);

        return repairTicketMapper.toDTO(updated);
    }

    public RepairTicketDTO changeStatus(Long ticketId, TicketStatus newStatus) {
        RepairTicket ticket = findTicketById(ticketId);
        ticket.changeStatus(newStatus);

        RepairTicket updated = repairTicketRepository.save(ticket);
        log.info("Ticket {} status changed to {}", ticketId, newStatus);

        return repairTicketMapper.toDTO(updated);
    }

    public RepairTicketDTO close(Long ticketId, CloseTicketDTO closeDTO) {
        RepairTicket ticket = findTicketById(ticketId);

        // Verify all interventions are completed
        boolean allCompleted = ticket.getInterventions().stream()
                .allMatch(i -> i.getStatus() == InterventionStatus.COMPLETED);

        if (!allCompleted) {
            throw new IllegalStateException("All interventions must be COMPLETED before closing ticket");
        }

        // Close ticket and assign grade
        ticket.close(closeDTO.getGrade());

        RepairTicket updated = repairTicketRepository.save(ticket);
        log.info("Ticket {} closed with grade {}", ticketId, closeDTO.getGrade());

        return repairTicketMapper.toDTO(updated);
    }

    public List<RepairTicketDTO> findOverdueUrgentTickets() {
        LocalDateTime threshold = LocalDateTime.now().minusHours(2);
        return repairTicketRepository.findOverdueUrgentTickets(threshold).stream()
                .map(repairTicketMapper::toDTO)
                .toList();
    }

    private RepairTicket findTicketById(Long id) {
        return repairTicketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Repair ticket not found: " + id));
    }
}