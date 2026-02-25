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
public class ProductEventListener {

    private final NotificationService notificationService;

    @EventListener
    @Async
    public void handleProductStatusChanged(ProductStatusChangedEvent event) {
        log.info("Product {} status changed from {} to {}",
                event.getProduct().getSerialNumber(),
                event.getOldStatus(),
                event.getNewStatus());

        // Send notification if product is sold
        if (event.getNewStatus() == com.yann.phoenix_exchange_api.entity.product.ProductStatus.SOLD) {
            // TODO: Send notification
            log.info("Product sold notification sent");
        }
    }
}