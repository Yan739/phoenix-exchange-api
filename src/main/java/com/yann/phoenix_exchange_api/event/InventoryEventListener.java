package com.yann.phoenix_exchange_api.event;

import com.yann.phoenix_exchange_api.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class InventoryEventListener {

    private final NotificationService notificationService;

    @EventListener
    @Async
    public void handleLowStock(LowStockEvent event) {
        log.warn("Low stock alert for warehouse: {} ({}/{})",
                event.getWarehouse().getName(),
                event.getCurrentStock(),
                event.getCapacity());

        notificationService.alertWarehouseCapacity(event.getWarehouse());
    }
}