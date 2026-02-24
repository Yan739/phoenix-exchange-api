package com.yann.phoenix_exchange_api.mapper;

import com.yann.phoenix_exchange_api.dto.sale.OrderItemCreateDTO;
import com.yann.phoenix_exchange_api.dto.sale.OrderItemDTO;
import com.yann.phoenix_exchange_api.entity.sale.OrderItem;
import org.springframework.stereotype.Component;

@Component
public class OrderItemMapper {

    public OrderItemDTO toDTO(OrderItem entity) {
        if (entity == null) return null;

        return OrderItemDTO.builder()
                .id(entity.getId())
                .orderId(entity.getOrder().getId())
                .productId(entity.getProduct().getId())
                .productName(entity.getProduct().getName())
                .productSerialNumber(entity.getProduct().getSerialNumber())
                .quantity(entity.getQuantity())
                .unitPrice(entity.getUnitPrice())
                .totalPrice(entity.getTotalPrice())
                .build();
    }

    public OrderItem toEntity(OrderItemCreateDTO dto) {
        if (dto == null) return null;

        return OrderItem.builder()
                .quantity(dto.getQuantity())
                .unitPrice(dto.getUnitPrice())
                .build();
    }
}