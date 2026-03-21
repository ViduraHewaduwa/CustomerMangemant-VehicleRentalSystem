package com.example.Customer_Service.repository;

import com.example.Customer_Service.entity.ServiceHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ServiceHistoryRepository extends JpaRepository<ServiceHistory, Long> {
    List<ServiceHistory> findByCustomerIdOrderByServiceDateDesc(Long customerId);
    Optional<ServiceHistory> findByPaymentId(String paymentId);
}
