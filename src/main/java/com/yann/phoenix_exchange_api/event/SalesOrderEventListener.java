package com.yann.phoenix_exchange_api.event;

import com.yann.phoenix_exchange_api.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
@Slf4j
public class SalesOrderEventListener {

    private final NotificationService notificationService;

    @EventListener
    @Async
    public void handleSalesOrderCreated(SalesOrderCreatedEvent event) {
        log.info("Sales order created: {}", event.getOrder().getOrderNumber());

        // Alert if high-value order
        if (event.getOrder().getTotalAmount().compareTo(new BigDecimal("5000")) > 0) {
            notificationService.alertHighValueOrder(event.getOrder());
        }
    }

    @EventListener
    @Async
    public void handlePaymentConfirmed(PaymentConfirmedEvent event) {
        log.info("Payment confirmed for order: {}",
                event.getPayment().getOrder().getOrderNumber());

        // Send confirmation email to customer
        // TODO: Implement email service
    }
}