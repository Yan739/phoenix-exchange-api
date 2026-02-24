package com.yann.phoenix_exchange_api.mapper;

import com.yann.phoenix_exchange_api.dto.sale.PaymentCreateDTO;
import com.yann.phoenix_exchange_api.dto.sale.PaymentDTO;
import com.yann.phoenix_exchange_api.entity.sale.Payment;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public PaymentDTO toDTO(Payment entity) {
        if (entity == null) return null;

        return PaymentDTO.builder()
                .id(entity.getId())
                .orderId(entity.getOrder().getId())
                .orderNumber(entity.getOrder().getOrderNumber())
                .amount(entity.getAmount())
                .method(entity.getMethod())
                .status(entity.getStatus())
                .transactionId(entity.getTransactionId())
                .reference(entity.getReference())
                .paidAt(entity.getPaidAt())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public Payment toEntity(PaymentCreateDTO dto) {
        if (dto == null) return null;

        return Payment.builder()
                .amount(dto.getAmount())
                .method(dto.getMethod())
                .reference(dto.getReference())
                .build();
    }
}