package com.yann.phoenix_exchange_api.repository;

import com.yann.phoenix_exchange_api.entity.product.Grade;
import com.yann.phoenix_exchange_api.entity.product.Product;
import com.yann.phoenix_exchange_api.entity.product.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    boolean existsBySerialNumber(String serialNumber);

    Optional<Product> findBySerialNumber(String serialNumber);

    @Query("SELECT p FROM Product p WHERE " +
            "(:category IS NULL OR p.category = :category) AND " +
            "(:brand IS NULL OR p.brand = :brand) AND " +
            "(:status IS NULL OR p.status = :status)")
    Page<Product> findByFilters(
            @Param("category") String category,
            @Param("brand") String brand,
            @Param("status") ProductStatus status,
            Pageable pageable
    );

    List<Product> findByStatus(ProductStatus status);

    Long countByStatus(ProductStatus status);

    Long countByGrade(Grade grade);
}