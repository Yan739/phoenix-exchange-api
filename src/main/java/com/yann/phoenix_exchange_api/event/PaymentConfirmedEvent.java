package com.yann.phoenix_exchange_api.event;

import com.yann.phoenix_exchange_api.entity.sale.Payment;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PaymentConfirmedEvent extends ApplicationEvent {
    private final Payment payment;

    public PaymentConfirmedEvent(Object source, Payment payment) {
        super(source);
        this.payment = payment;
    }
}