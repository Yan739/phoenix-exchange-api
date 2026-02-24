package com.yann.phoenix_exchange_api.service;

import com.yann.phoenix_exchange_api.dto.purchase.PurchaseOrderCreateDTO;
import com.yann.phoenix_exchange_api.dto.purchase.PurchaseOrderDTO;
import com.yann.phoenix_exchange_api.entity.purchase.OrderStatus;
import com.yann.phoenix_exchange_api.entity.purchase.PurchaseOrder;
import com.yann.phoenix_exchange_api.entity.purchase.Supplier;
import com.yann.phoenix_exchange_api.entity.user.User;
import com.yann.phoenix_exchange_api.exception.ResourceNotFoundException;
import com.yann.phoenix_exchange_api.mapper.PurchaseOrderMapper;
import com.yann.phoenix_exchange_api.repository.PurchaseOrderRepository;
import com.yann.phoenix_exchange_api.repository.SupplierRepository;
import com.yann.phoenix_exchange_api.repository.UserRepository;
import com.yann.phoenix_exchange_api.security.SecurityUtils;
import com.yann.phoenix_exchange_api.utils.NumberGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final SupplierRepository supplierRepository;
    private final UserRepository userRepository;
    private final PurchaseOrderMapper purchaseOrderMapper;
    private final SecurityUtils securityUtils;

    public PurchaseOrderDTO getById(Long id) {
        PurchaseOrder order = findOrderById(id);
        return purchaseOrderMapper.toDTO(order);
    }

    public Page<PurchaseOrderDTO> getAll(Pageable pageable) {
        return purchaseOrderRepository.findAll(pageable)
                .map(purchaseOrderMapper::toDTO);
    }

    public List<PurchaseOrderDTO> getByStatus(OrderStatus status) {
        return purchaseOrderRepository.findByStatus(status).stream()
                .map(purchaseOrderMapper::toDTO)
                .toList();
    }

    public List<PurchaseOrderDTO> getBySupplier(Long supplierId) {
        return purchaseOrderRepository.findBySupplierId(supplierId).stream()
                .map(purchaseOrderMapper::toDTO)
                .toList();
    }

    public PurchaseOrderDTO create(PurchaseOrderCreateDTO createDTO) {
        Supplier supplier = supplierRepository.findById(createDTO.getSupplierId())
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found"));

        User currentUser = securityUtils.getCurrentUser()
                .orElseThrow(() -> new IllegalStateException("User not authenticated"));

        PurchaseOrder order = purchaseOrderMapper.toEntity(createDTO);
        order.setSupplier(supplier);
        order.setOrderNumber(NumberGenerator.generatePurchaseOrderNumber());
        order.setStatus(OrderStatus.DRAFT);
        order.setCreatedBy(currentUser);

        PurchaseOrder saved = purchaseOrderRepository.save(order);
        log.info("Purchase order created: {}", saved.getOrderNumber());

        return purchaseOrderMapper.toDTO(saved);
    }

    public PurchaseOrderDTO approve(Long orderId) {
        PurchaseOrder order = findOrderById(orderId);

        User approver = securityUtils.getCurrentUser()
                .orElseThrow(() -> new IllegalStateException("User not authenticated"));

        order.approve(approver);

        // Update supplier total purchases
        Supplier supplier = order.getSupplier();
        supplier.addPurchase(order.getTotalAmount());
        supplierRepository.save(supplier);

        PurchaseOrder updated = purchaseOrderRepository.save(order);
        log.info("Purchase order {} approved by {}", orderId, approver.getUsername());

        return purchaseOrderMapper.toDTO(updated);
    }

    public PurchaseOrderDTO receive(Long orderId) {
        PurchaseOrder order = findOrderById(orderId);
        order.receive();

        PurchaseOrder updated = purchaseOrderRepository.save(order);
        log.info("Purchase order {} received", orderId);

        return purchaseOrderMapper.toDTO(updated);
    }

    public void cancel(Long orderId) {
        PurchaseOrder order = findOrderById(orderId);
        order.cancel();
        purchaseOrderRepository.save(order);
        log.info("Purchase order {} cancelled", orderId);
    }

    private PurchaseOrder findOrderById(Long id) {
        return purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase order not found: " + id));
    }
}