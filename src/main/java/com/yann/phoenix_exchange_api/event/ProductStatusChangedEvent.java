package com.yann.phoenix_exchange_api.event;

import com.yann.phoenix_exchange_api.entity.product.Product;
import com.yann.phoenix_exchange_api.entity.product.ProductStatus;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ProductStatusChangedEvent extends ApplicationEvent {
    private final Product product;
    private final ProductStatus oldStatus;
    private final ProductStatus newStatus;

    public ProductStatusChangedEvent(Object source, Product product,
                                     ProductStatus oldStatus, ProductStatus newStatus) {
        super(source);
        this.product = product;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
    }
}