package com.yann.phoenix_exchange_api.repository;

import com.yann.phoenix_exchange_api.entity.product.Grade;
import com.yann.phoenix_exchange_api.entity.product.Product;
import com.yann.phoenix_exchange_api.entity.product.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    boolean existsBySerialNumber(String serialNumber);
    Optional<Product> findBySerialNumber(String serialNumber);
    Page<Product> findByFilters(String category, String brand, ProductStatus status, Pageable pageable);
    List<Product> findByStatus(ProductStatus status);
    Long countByStatus(ProductStatus status);
    Long countByGrade(Grade grade);
}
