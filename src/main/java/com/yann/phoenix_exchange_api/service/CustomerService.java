package com.yann.phoenix_exchange_api.service;

import com.yann.phoenix_exchange_api.dto.customer.CustomerCreateDTO;
import com.yann.phoenix_exchange_api.dto.customer.CustomerDTO;
import com.yann.phoenix_exchange_api.dto.customer.CustomerUpdateDTO;
import com.yann.phoenix_exchange_api.entity.sale.Customer;
import com.yann.phoenix_exchange_api.entity.sale.CustomerSegment;
import com.yann.phoenix_exchange_api.entity.sale.CustomerType;
import com.yann.phoenix_exchange_api.exception.ResourceNotFoundException;
import com.yann.phoenix_exchange_api.mapper.CustomerMapper;
import com.yann.phoenix_exchange_api.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public CustomerDTO getById(Long id) {
        Customer customer = findCustomerById(id);
        return customerMapper.toDTO(customer);
    }

    public Page<CustomerDTO> getAll(Pageable pageable) {
        return customerRepository.findAll(pageable)
                .map(customerMapper::toDTO);
    }

    public List<CustomerDTO> getByType(CustomerType type) {
        return customerRepository.findByType(type).stream()
                .map(customerMapper::toDTO)
                .toList();
    }

    public List<CustomerDTO> getBySegment(CustomerSegment segment) {
        return customerRepository.findBySegment(segment).stream()
                .map(customerMapper::toDTO)
                .toList();
    }

    public List<CustomerDTO> getVIPCustomers() {
        return customerRepository.findVIPCustomers().stream()
                .map(customerMapper::toDTO)
                .toList();
    }

    public CustomerDTO create(CustomerCreateDTO createDTO) {
        Customer customer = customerMapper.toEntity(createDTO);

        Customer saved = customerRepository.save(customer);
        log.info("Customer created: {}", saved.getName());

        return customerMapper.toDTO(saved);
    }

    public CustomerDTO update(Long id, CustomerUpdateDTO updateDTO) {
        Customer customer = findCustomerById(id);
        customerMapper.updateEntityFromDTO(updateDTO, customer);

        Customer updated = customerRepository.save(customer);
        log.info("Customer updated: {}", id);

        return customerMapper.toDTO(updated);
    }

    public void delete(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Customer not found: " + id);
        }

        customerRepository.deleteById(id);
        log.info("Customer deleted: {}", id);
    }

    public void addLoyaltyPoints(Long id, Integer points) {
        Customer customer = findCustomerById(id);
        customer.addLoyaltyPoints(points);
        customerRepository.save(customer);
        log.info("Added {} loyalty points to customer {}", points, id);
    }

    public void addSpending(Long id, BigDecimal amount) {
        Customer customer = findCustomerById(id);
        customer.addSpending(amount);
        customerRepository.save(customer);
        log.info("Added spending {} to customer {}", amount, id);
    }

    private Customer findCustomerById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + id));
    }
}