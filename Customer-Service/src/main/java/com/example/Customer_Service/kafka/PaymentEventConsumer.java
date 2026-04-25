package com.example.Customer_Service.kafka;

import com.example.Customer_Service.dto.PaymentCompletedEvent;
import com.example.Customer_Service.service.PaymentEventService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(PaymentEventConsumer.class);

    private final ObjectMapper objectMapper;
    private final PaymentEventService paymentEventService;

    public PaymentEventConsumer(ObjectMapper objectMapper, PaymentEventService paymentEventService) {
        this.objectMapper = objectMapper;
        this.paymentEventService = paymentEventService;
    }

    @KafkaListener(topics = "${app.kafka.payment-topic}")
    public void consumePaymentCompletedEvent(String message) {
        try {
            PaymentCompletedEvent event = objectMapper.readValue(message, PaymentCompletedEvent.class);
            paymentEventService.savePaymentHistory(event);
            log.info("Processed payment event for customer {}", event.getCustomerId());
        } catch (JsonProcessingException exception) {
            log.error("Failed to deserialize payment event: {}", message, exception);
        }
    }
}
