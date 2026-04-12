package com.example.Customer_Service.service;

import com.example.Customer_Service.dto.PaymentCompletedEvent;
import com.example.Customer_Service.entity.Customer;
import com.example.Customer_Service.entity.ServiceHistory;
import com.example.Customer_Service.repository.CustomerRepository;
import com.example.Customer_Service.repository.ServiceHistoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class PaymentEventService {

    private static final Logger log = LoggerFactory.getLogger(PaymentEventService.class);

    private final CustomerRepository customerRepository;
    private final ServiceHistoryRepository serviceHistoryRepository;

    public PaymentEventService(CustomerRepository customerRepository,
                               ServiceHistoryRepository serviceHistoryRepository) {
        this.customerRepository = customerRepository;
        this.serviceHistoryRepository = serviceHistoryRepository;
    }

    @Transactional
    public void savePaymentHistory(PaymentCompletedEvent event) {
        if (event.getCustomerId() == null) {
            log.warn("Skipping payment event because customerId is missing");
            return;
        }

        Customer customer = customerRepository.findById(event.getCustomerId()).orElse(null);
        if (customer == null) {
            log.warn("Skipping payment event because customer {} was not found", event.getCustomerId());
            return;
        }

        ServiceHistory history = resolveHistory(event);
        history.setCustomer(customer);
        history.setPaymentId(blankToNull(event.getPaymentId()));
        history.setServiceDate(event.getServiceDate() != null ? event.getServiceDate() : LocalDate.now());
        history.setVehicle(defaultValue(event.getVehicle(), "Unknown vehicle"));
        history.setDescription(defaultValue(event.getDescription(), "Payment received"));
        history.setStatus(defaultValue(event.getStatus(), "PAID"));
        history.setCost(event.getCost() != null ? event.getCost() : java.math.BigDecimal.ZERO);

        serviceHistoryRepository.save(history);
    }

    private ServiceHistory resolveHistory(PaymentCompletedEvent event) {
        String paymentId = blankToNull(event.getPaymentId());
        if (paymentId == null) {
            return new ServiceHistory();
        }

        return serviceHistoryRepository.findByPaymentId(paymentId)
                .orElseGet(ServiceHistory::new);
    }

    private String defaultValue(String value, String fallback) {
        String trimmed = blankToNull(value);
        return trimmed != null ? trimmed : fallback;
    }

    private String blankToNull(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
