package com.yann.phoenix_exchange_api.mapper;

import com.yann.phoenix_exchange_api.dto.sale.SalesOrderCreateDTO;
import com.yann.phoenix_exchange_api.dto.sale.SalesOrderDTO;
import com.yann.phoenix_exchange_api.dto.sale.SalesOrderUpdateDTO;
import com.yann.phoenix_exchange_api.entity.sale.SalesOrder;
import com.yann.phoenix_exchange_api.entity.sale.SalesOrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SalesOrderMapper {

    private final OrderItemMapper orderItemMapper;
    private final PaymentMapper paymentMapper;

    public SalesOrderDTO toDTO(SalesOrder entity) {
        if (entity == null) return null;

        return SalesOrderDTO.builder()
                .id(entity.getId())
                .orderNumber(entity.getOrderNumber())
                .customerId(entity.getCustomer().getId())
                .customerName(entity.getCustomer().getName())
                .totalAmount(entity.getTotalAmount())
                .taxAmount(entity.getTaxAmount())
                .discountAmount(entity.getDiscountAmount())
                .grandTotal(calculateGrandTotal(entity))
                .status(entity.getStatus())
                .paymentMethod(entity.getPaymentMethod())
                .paymentStatus(entity.getPaymentStatus())
                .orderItems(entity.getOrderItems().stream()
                        .map(orderItemMapper::toDTO)
                        .collect(Collectors.toList()))
                .itemsCount(entity.getOrderItems().size())
                .payments(entity.getPayments().stream()
                        .map(paymentMapper::toDTO)
                        .collect(Collectors.toList()))
                .paymentsCount(entity.getPayments().size())
                .notes(entity.getNotes())
                .createdAt(entity.getCreatedAt())
                .deliveredAt(entity.getDeliveredAt())
                .daysSinceCreation(calculateDaysSinceCreation(entity))
                .canBeCancelled(canBeCancelled(entity))
                .build();
    }

    public SalesOrder toEntity(SalesOrderCreateDTO dto) {
        if (dto == null) return null;

        return SalesOrder.builder()
                .paymentMethod(dto.getPaymentMethod())
                .discountAmount(dto.getDiscountAmount())
                .notes(dto.getNotes())
                .build();
    }

    public void updateEntityFromDTO(SalesOrderUpdateDTO dto, SalesOrder entity) {
        if (dto == null || entity == null) return;

        if (dto.getStatus() != null) entity.setStatus(dto.getStatus());
        if (dto.getPaymentMethod() != null) entity.setPaymentMethod(dto.getPaymentMethod());
        if (dto.getDiscountAmount() != null) entity.setDiscountAmount(dto.getDiscountAmount());
        if (dto.getNotes() != null) entity.setNotes(dto.getNotes());
    }

    private BigDecimal calculateGrandTotal(SalesOrder entity) {
        BigDecimal total = entity.getTotalAmount();
        if (entity.getTaxAmount() != null) {
            total = total.add(entity.getTaxAmount());
        }
        return total;
    }

    private Integer calculateDaysSinceCreation(SalesOrder entity) {
        if (entity.getCreatedAt() == null) return null;
        return (int) ChronoUnit.DAYS.between(entity.getCreatedAt(), LocalDateTime.now());
    }

    private Boolean canBeCancelled(SalesOrder entity) {
        return entity.getStatus() != SalesOrderStatus.DELIVERED &&
                entity.getStatus() != SalesOrderStatus.CANCELLED;
    }
}