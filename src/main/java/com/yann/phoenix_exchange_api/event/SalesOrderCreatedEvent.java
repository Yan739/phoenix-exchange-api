package com.yann.phoenix_exchange_api.event;

import com.yann.phoenix_exchange_api.entity.sale.SalesOrder;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class SalesOrderCreatedEvent extends ApplicationEvent {
    private final SalesOrder order;

    public SalesOrderCreatedEvent(Object source, SalesOrder order) {
        super(source);
        this.order = order;
    }
}