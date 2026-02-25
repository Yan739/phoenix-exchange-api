package com.yann.phoenix_exchange_api.event;

import com.yann.phoenix_exchange_api.entity.repair.Priority;
import com.yann.phoenix_exchange_api.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RepairTicketEventListener {

    private final NotificationService notificationService;

    @EventListener
    @Async
    public void handleRepairTicketCreated(RepairTicketCreatedEvent event) {
        log.info("Repair ticket created: {}", event.getTicket().getTicketNumber());

        // Alert if urgent ticket
        if (event.getTicket().getPriority() == Priority.URGENT) {
            notificationService.alertUrgentTicketCreated(event.getTicket());
        }
    }

    @EventListener
    @Async
    public void handleRepairTicketClosed(RepairTicketClosedEvent event) {
        log.info("Repair ticket closed: {} with grade {}",
                event.getTicket().getTicketNumber(),
                event.getTicket().getProduct().getGrade());

        // Update statistics
        // TODO: Implement
    }
}