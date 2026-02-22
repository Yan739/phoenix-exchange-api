package com.yann.phoenix_exchange_api.repository;

import com.yann.phoenix_exchange_api.entity.sale.Customer;
import com.yann.phoenix_exchange_api.entity.sale.CustomerSegment;
import com.yann.phoenix_exchange_api.entity.sale.CustomerType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    List<Customer> findByType(CustomerType type);
    List<Customer> findBySegment(CustomerSegment segment);
    List<Customer> findByIsActive(Boolean isActive);
    Optional<Customer> findByEmail(String email);

    @Query("SELECT c FROM Customer c WHERE c.segment = 'VIP' ORDER BY c.totalSpent DESC")
    List<Customer> findVIPCustomers();
}
