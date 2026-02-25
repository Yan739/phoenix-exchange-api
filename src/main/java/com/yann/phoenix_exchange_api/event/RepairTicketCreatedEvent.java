package com.yann.phoenix_exchange_api.event;

import com.yann.phoenix_exchange_api.entity.repair.RepairTicket;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class RepairTicketCreatedEvent extends ApplicationEvent {
    private final RepairTicket ticket;

    public RepairTicketCreatedEvent(Object source, RepairTicket ticket) {
        super(source);
        this.ticket = ticket;
    }
}