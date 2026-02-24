package com.yann.phoenix_exchange_api.service;

import com.yann.phoenix_exchange_api.dto.dashboard.AlertDTO;
import com.yann.phoenix_exchange_api.entity.inventory.Warehouse;
import com.yann.phoenix_exchange_api.entity.product.ProductStatus;
import com.yann.phoenix_exchange_api.entity.repair.RepairTicket;
import com.yann.phoenix_exchange_api.entity.sale.SalesOrder;
import com.yann.phoenix_exchange_api.entity.sale.SalesOrderStatus;
import com.yann.phoenix_exchange_api.repository.ProductRepository;
import com.yann.phoenix_exchange_api.repository.RepairTicketRepository;
import com.yann.phoenix_exchange_api.repository.SalesOrderRepository;
import com.yann.phoenix_exchange_api.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class NotificationService {

    private final RepairTicketRepository repairTicketRepository;
    private final WarehouseRepository warehouseRepository;
    private final SalesOrderRepository salesOrderRepository;
    private final ProductRepository productRepository;

    /**
     * Get all active alerts
     */
    public List<AlertDTO> getActiveAlerts() {
        List<AlertDTO> alerts = new ArrayList<>();

        // Check urgent unassigned tickets
        alerts.addAll(getUrgentTicketAlerts());

        // Check overdue tickets
        alerts.addAll(getOverdueTicketAlerts());

        // Check warehouses near capacity
        alerts.addAll(getWarehouseCapacityAlerts());

        // Check pending high-value orders
        alerts.addAll(getHighValueOrderAlerts());

        // Check low stock alerts
        alerts.addAll(getLowStockAlerts());

        return alerts;
    }

    /**
     * Get urgent ticket alerts
     */
    private List<AlertDTO> getUrgentTicketAlerts() {
        List<AlertDTO> alerts = new ArrayList<>();
        LocalDateTime threshold = LocalDateTime.now().minusHours(2);

        List<RepairTicket> urgentTickets = repairTicketRepository
                .findOverdueUrgentTickets(threshold);

        for (RepairTicket ticket : urgentTickets) {
            alerts.add(AlertDTO.builder()
                    .type("URGENT_TICKET")
                    .severity("CRITICAL")
                    .title("Ticket urgent non assigné")
                    .message(String.format("Le ticket %s (priorité URGENT) n'est pas assigné depuis plus de 2h",
                            ticket.getTicketNumber()))
                    .actionUrl("/repair-tickets/" + ticket.getId())
                    .actionLabel("Voir le ticket")
                    .referenceId(ticket.getId())
                    .referenceType("TICKET")
                    .createdAt(LocalDateTime.now())
                    .isRead(false)
                    .isDismissed(false)
                    .build());
        }

        return alerts;
    }

    /**
     * Get overdue ticket alerts
     */
    private List<AlertDTO> getOverdueTicketAlerts() {
        List<AlertDTO> alerts = new ArrayList<>();

        List<RepairTicket> overdueTickets = repairTicketRepository.findAll().stream()
                .filter(ticket -> ticket.isOverdue())
                .toList();

        for (RepairTicket ticket : overdueTickets) {
            alerts.add(AlertDTO.builder()
                    .type("OVERDUE_REPAIR")
                    .severity("WARNING")
                    .title("Réparation en retard")
                    .message(String.format("Le ticket %s dépasse la durée estimée",
                            ticket.getTicketNumber()))
                    .actionUrl("/repair-tickets/" + ticket.getId())
                    .actionLabel("Voir le ticket")
                    .referenceId(ticket.getId())
                    .referenceType("TICKET")
                    .createdAt(LocalDateTime.now())
                    .isRead(false)
                    .isDismissed(false)
                    .build());
        }

        return alerts;
    }

    /**
     * Get warehouse capacity alerts
     */
    private List<AlertDTO> getWarehouseCapacityAlerts() {
        List<AlertDTO> alerts = new ArrayList<>();

        List<Warehouse> nearCapacity = warehouseRepository.findAll().stream()
                .filter(w -> w.getOccupancyRate() > 80.0)
                .toList();

        for (Warehouse warehouse : nearCapacity) {
            String severity = warehouse.getOccupancyRate() > 90.0 ? "CRITICAL" : "WARNING";

            alerts.add(AlertDTO.builder()
                    .type("LOW_STOCK")
                    .severity(severity)
                    .title("Entrepôt presque plein")
                    .message(String.format("L'entrepôt %s est rempli à %.0f%%",
                            warehouse.getName(), warehouse.getOccupancyRate()))
                    .actionUrl("/warehouses/" + warehouse.getId())
                    .actionLabel("Voir l'entrepôt")
                    .referenceId(warehouse.getId())
                    .referenceType("WAREHOUSE")
                    .createdAt(LocalDateTime.now())
                    .isRead(false)
                    .isDismissed(false)
                    .build());
        }

        return alerts;
    }

    /**
     * Get high-value order alerts
     */
    private List<AlertDTO> getHighValueOrderAlerts() {
        List<AlertDTO> alerts = new ArrayList<>();

        List<SalesOrder> highValueOrders = salesOrderRepository.findAll().stream()
                .filter(order -> order.getStatus() == SalesOrderStatus.PENDING_PAYMENT)
                .filter(order -> order.getTotalAmount().compareTo(new java.math.BigDecimal("5000")) > 0)
                .toList();

        for (SalesOrder order : highValueOrders) {
            alerts.add(AlertDTO.builder()
                    .type("HIGH_VALUE_ORDER")
                    .severity("INFO")
                    .title("Commande de valeur élevée")
                    .message(String.format("Commande %s de %.2f€ en attente de paiement",
                            order.getOrderNumber(), order.getTotalAmount()))
                    .actionUrl("/sales-orders/" + order.getId())
                    .actionLabel("Voir la commande")
                    .referenceId(order.getId())
                    .referenceType("ORDER")
                    .createdAt(LocalDateTime.now())
                    .isRead(false)
                    .isDismissed(false)
                    .build());
        }

        return alerts;
    }

    /**
     * Get low stock alerts
     */
    private List<AlertDTO> getLowStockAlerts() {
        List<AlertDTO> alerts = new ArrayList<>();

        // Check if stock of refurbished products is low
        Long refurbishedCount = productRepository.countByStatus(ProductStatus.REFURBISHED);
        Long inStockCount = productRepository.countByStatus(ProductStatus.IN_STOCK);
        Long availableStock = refurbishedCount + inStockCount;

        if (availableStock < 10) {
            alerts.add(AlertDTO.builder()
                    .type("LOW_STOCK")
                    .severity("WARNING")
                    .title("Stock bas")
                    .message(String.format("Seulement %d produits disponibles à la vente", availableStock))
                    .actionUrl("/products?status=IN_STOCK,REFURBISHED")
                    .actionLabel("Voir les produits")
                    .referenceId(null)
                    .referenceType("PRODUCT")
                    .createdAt(LocalDateTime.now())
                    .isRead(false)
                    .isDismissed(false)
                    .build());
        }

        return alerts;
    }

    /**
     * Send notification (placeholder - implement with email/SMS service)
     */
    @Transactional
    public void sendNotification(Long userId, String subject, String message) {
        // TODO: Implement actual notification sending
        // - Email via SMTP
        // - SMS via Twilio
        // - Push notification
        // - In-app notification
        log.info("Notification sent to user {}: {}", userId, subject);
    }

    /**
     * Send alert when urgent ticket is created
     */
    @Transactional
    public void alertUrgentTicketCreated(RepairTicket ticket) {
        // Find all managers and send notification
        log.info("ALERT: Urgent ticket created: {}", ticket.getTicketNumber());
        // TODO: Send email/SMS to managers
    }

    /**
     * Send alert when warehouse is near capacity
     */
    @Transactional
    public void alertWarehouseCapacity(Warehouse warehouse) {
        log.info("ALERT: Warehouse {} is {}% full",
                warehouse.getName(), warehouse.getOccupancyRate());
        // TODO: Send notification to warehouse managers
    }

    /**
     * Send alert for high-value order
     */
    @Transactional
    public void alertHighValueOrder(SalesOrder order) {
        log.info("ALERT: High-value order created: {} ({}€)",
                order.getOrderNumber(), order.getTotalAmount());
        // TODO: Send notification to managers
    }
}