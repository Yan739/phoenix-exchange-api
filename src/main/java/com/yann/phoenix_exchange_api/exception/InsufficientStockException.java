package com.yann.phoenix_exchange_api.exception;

public class InsufficientStockException extends BusinessException {

    public InsufficientStockException(String message) {
        super(message, "INSUFFICIENT_STOCK");
    }

    public InsufficientStockException(Long productId, Integer requested, Integer available) {
        super(String.format("Insufficient stock for product %d. Requested: %d, Available: %d",
                productId, requested, available), "INSUFFICIENT_STOCK");
    }
}