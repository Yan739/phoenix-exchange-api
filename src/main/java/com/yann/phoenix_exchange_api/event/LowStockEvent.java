package com.yann.phoenix_exchange_api.event;

import com.yann.phoenix_exchange_api.entity.inventory.Warehouse;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class LowStockEvent extends ApplicationEvent {
    private final Warehouse warehouse;
    private final Integer currentStock;
    private final Integer capacity;

    public LowStockEvent(Object source, Warehouse warehouse) {
        super(source);
        this.warehouse = warehouse;
        this.currentStock = warehouse.getCurrentStock();
        this.capacity = warehouse.getCapacity();
    }
}