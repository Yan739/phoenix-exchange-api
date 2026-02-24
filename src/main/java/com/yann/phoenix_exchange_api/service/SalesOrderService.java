package com.yann.phoenix_exchange_api.service;

import com.yann.phoenix_exchange_api.dto.sale.OrderItemCreateDTO;
import com.yann.phoenix_exchange_api.dto.sale.SalesOrderCreateDTO;
import com.yann.phoenix_exchange_api.dto.sale.SalesOrderDTO;
import com.yann.phoenix_exchange_api.entity.product.Product;
import com.yann.phoenix_exchange_api.entity.product.ProductStatus;
import com.yann.phoenix_exchange_api.entity.sale.*;
import com.yann.phoenix_exchange_api.exception.ResourceNotFoundException;
import com.yann.phoenix_exchange_api.mapper.SalesOrderMapper;
import com.yann.phoenix_exchange_api.repository.CustomerRepository;
import com.yann.phoenix_exchange_api.repository.PaymentRepository;
import com.yann.phoenix_exchange_api.repository.ProductRepository;
import com.yann.phoenix_exchange_api.repository.SalesOrderRepository;
import com.yann.phoenix_exchange_api.utils.NumberGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SalesOrderService {

    private final SalesOrderRepository salesOrderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final PaymentRepository paymentRepository;
    private final SalesOrderMapper salesOrderMapper;
    private final CustomerService customerService;

    public SalesOrderDTO getById(Long id) {
        SalesOrder order = findOrderById(id);
        return salesOrderMapper.toDTO(order);
    }

    public Page<SalesOrderDTO> getAll(Pageable pageable) {
        return salesOrderRepository.findAll(pageable)
                .map(salesOrderMapper::toDTO);
    }

    public Page<SalesOrderDTO> getByCustomer(Long customerId, Pageable pageable) {
        return salesOrderRepository.findByCustomerId(customerId, pageable)
                .map(salesOrderMapper::toDTO);
    }

    public List<SalesOrderDTO> getByStatus(SalesOrderStatus status) {
        return salesOrderRepository.findByStatus(status).stream()
                .map(salesOrderMapper::toDTO)
                .toList();
    }

    public SalesOrderDTO create(SalesOrderCreateDTO createDTO) {
        Customer customer = customerRepository.findById(createDTO.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        SalesOrder order = salesOrderMapper.toEntity(createDTO);
        order.setCustomer(customer);
        order.setOrderNumber(NumberGenerator.generateSalesOrderNumber());
        order.setStatus(SalesOrderStatus.DRAFT);
        order.setPaymentStatus(PaymentStatus.PENDING);

        List<OrderItem> items = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItemCreateDTO itemDTO : createDTO.getItems()) {
            Product product = productRepository.findById(itemDTO.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

            // Verify product is available for sale
            if (!product.isAvailableForSale()) {
                throw new IllegalStateException("Product not available: " + product.getSerialNumber());
            }

            // Reserve product
            product.changeStatus(ProductStatus.RESERVED);
            productRepository.save(product);

            OrderItem item = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(itemDTO.getQuantity())
                    .unitPrice(itemDTO.getUnitPrice())
                    .totalPrice(itemDTO.getUnitPrice().multiply(new BigDecimal(itemDTO.getQuantity())))
                    .build();

            items.add(item);
            totalAmount = totalAmount.add(item.getTotalPrice());
        }

        order.setOrderItems(items);
        order.setTotalAmount(totalAmount);
        order.recalculateTotal();

        SalesOrder saved = salesOrderRepository.save(order);
        log.info("Sales order created: {}", saved.getOrderNumber());

        return salesOrderMapper.toDTO(saved);
    }

    public void confirmPayment(Long orderId, String transactionId) {
        SalesOrder order = findOrderById(orderId);

        // Create payment
        Payment payment = Payment.builder()
                .order(order)
                .amount(order.getTotalAmount())
                .status(PaymentStatus.COMPLETED)
                .transactionId(transactionId)
                .build();

        payment.process(transactionId);
        paymentRepository.save(payment);

        // Mark products as sold
        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            product.changeStatus(ProductStatus.SOLD);
            productRepository.save(product);
        }

        // Update customer spending
        customerService.addSpending(order.getCustomer().getId(), order.getTotalAmount());

        // Calculate and add loyalty points (1 point per 10€)
        int points = order.getTotalAmount().divide(new BigDecimal("10"), BigDecimal.ROUND_DOWN).intValue();
        customerService.addLoyaltyPoints(order.getCustomer().getId(), points);

        log.info("Payment confirmed for order {}", orderId);
    }

    public void ship(Long orderId) {
        SalesOrder order = findOrderById(orderId);
        order.ship();
        salesOrderRepository.save(order);
        log.info("Order {} shipped", orderId);
    }

    public void deliver(Long orderId) {
        SalesOrder order = findOrderById(orderId);
        order.deliver();
        salesOrderRepository.save(order);
        log.info("Order {} delivered", orderId);
    }

    public void cancel(Long orderId) {
        SalesOrder order = findOrderById(orderId);
        order.cancel();
        salesOrderRepository.save(order);
        log.info("Order {} cancelled", orderId);
    }

    private SalesOrder findOrderById(Long id) {
        return salesOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sales order not found: " + id));
    }
}