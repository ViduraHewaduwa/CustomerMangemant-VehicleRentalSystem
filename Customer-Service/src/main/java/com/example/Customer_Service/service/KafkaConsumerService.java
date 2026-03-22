package com.example.Customer_Service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);

    @KafkaListener(topics = "payment-success-topic", groupId = "${spring.kafka.consumer.group-id:customer-group}")
    public void listenPaymentSuccess(String message) {
        logger.info("Received Payment Success Event: {}", message);
        
        // TODO: Implement logic to handle the successful payment
        // For example: Update customer's rental status, send notification, etc.
        try {
            // Processing logic here
            handlePayment(message);
        } catch (Exception e) {
            logger.error("Error processing payment message: {}", e.getMessage());
        }
    }

    private void handlePayment(String message) {
        // Sample logic
        logger.info("Processing payment details for message: {}", message);
    }
}
