package com.yann.phoenix_exchange_api.service;

import com.yann.phoenix_exchange_api.dto.product.ProductCreateDTO;
import com.yann.phoenix_exchange_api.dto.product.ProductDTO;
import com.yann.phoenix_exchange_api.dto.product.ProductFilterDTO;
import com.yann.phoenix_exchange_api.dto.product.ProductUpdateDTO;
import com.yann.phoenix_exchange_api.entity.product.Product;
import com.yann.phoenix_exchange_api.entity.product.ProductStatus;
import com.yann.phoenix_exchange_api.entity.product.Grade;
import com.yann.phoenix_exchange_api.exception.ResourceNotFoundException;
import com.yann.phoenix_exchange_api.mapper.ProductMapper;
import com.yann.phoenix_exchange_api.repository.ProductRepository;
import com.yann.phoenix_exchange_api.repository.PurchaseOrderRepository;
import com.yann.phoenix_exchange_api.repository.SupplierRepository;
import com.yann.phoenix_exchange_api.repository.WarehouseRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;
    private final WarehouseRepository warehouseRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final ProductMapper productMapper;

    public ProductDTO getById(Long id) {
        Product product = findProductById(id);
        return productMapper.toDTO(product);
    }

    public ProductDTO getBySerialNumber(String serialNumber) {
        Product product = productRepository.findBySerialNumber(serialNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + serialNumber));
        return productMapper.toDTO(product);
    }

    public Page<ProductDTO> getAll(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(productMapper::toDTO);
    }

    public Page<ProductDTO> getByFilters(ProductFilterDTO filters, Pageable pageable) {
        return productRepository.findByFilters(
                filters.getCategory(),
                filters.getBrand(),
                filters.getStatus(),
                pageable
        ).map(productMapper::toDTO);
    }

    public List<ProductDTO> getByStatus(ProductStatus status) {
        return productRepository.findByStatus(status).stream()
                .map(productMapper::toDTO)
                .toList();
    }

    public ProductDTO create(ProductCreateDTO createDTO) {
        if (productRepository.existsBySerialNumber(createDTO.getSerialNumber())) {
            throw new IllegalArgumentException("Serial number already exists: " + createDTO.getSerialNumber());
        }

        Product product = productMapper.toEntity(createDTO);

        // Associate relations
        if (createDTO.getSupplierId() != null) {
            product.setSupplier(supplierRepository.findById(createDTO.getSupplierId())
                    .orElseThrow(() -> new ResourceNotFoundException("Supplier not found")));
        }

        if (createDTO.getPurchaseOrderId() != null) {
            product.setPurchaseOrder(purchaseOrderRepository.findById(createDTO.getPurchaseOrderId())
                    .orElseThrow(() -> new ResourceNotFoundException("Purchase order not found")));
        }

        if (createDTO.getWarehouseId() != null) {
            product.setWarehouse(warehouseRepository.findById(createDTO.getWarehouseId())
                    .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found")));
        }

        Product saved = productRepository.save(product);
        log.info("Product created: {}", saved.getSerialNumber());

        return productMapper.toDTO(saved);
    }

    public ProductDTO update(Long id, ProductUpdateDTO updateDTO) {
        Product product = findProductById(id);

        productMapper.updateEntityFromDTO(updateDTO, product);

        if (updateDTO.getWarehouseId() != null) {
            product.setWarehouse(warehouseRepository.findById(updateDTO.getWarehouseId())
                    .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found")));
        }

        Product updated = productRepository.save(product);
        log.info("Product updated: {}", id);

        return productMapper.toDTO(updated);
    }

    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found: " + id);
        }

        productRepository.deleteById(id);
        log.info("Product deleted: {}", id);
    }

    public void changeStatus(Long id, ProductStatus newStatus) {
        Product product = findProductById(id);
        product.changeStatus(newStatus);
        productRepository.save(product);
        log.info("Product {} status changed to {}", id, newStatus);
    }

    public void assignGrade(Long id, Grade grade) {
        Product product = findProductById(id);
        product.assignGrade(grade);
        productRepository.save(product);
        log.info("Product {} assigned grade {}", id, grade);
    }

    private Product findProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
    }
}