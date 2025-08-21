package com.kafka.microservices.paymentservice.handler;

import com.kafka.microservices.common.PaymentRequestEvent;
import com.kafka.microservices.common.Topics;
import com.kafka.microservices.paymentservice.services.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@KafkaListener(topics = {Topics.PAYMENT_REQUEST_TOPIC})
@Component
@RequiredArgsConstructor
public class PaymentHandler {
    private final PaymentService paymentService;

    @KafkaHandler
    @Transactional("transactionManager")
    public void handlePaymentRequest(@Payload PaymentRequestEvent requestEvent , @Header(KafkaHeaders.RECEIVED_KEY) String messageKey) {
        if (requestEvent.getOrderLineItems() == null || requestEvent.getOrderLineItems().isEmpty()) {
            throw new IllegalArgumentException("Order line items cannot be null or empty");
        }
        if (requestEvent.getTotalAmount() == null || requestEvent.getTotalAmount() <= 0) {
            throw new IllegalArgumentException("Total amount must be greater than zero");
        }
        paymentService.processPayment(messageKey, requestEvent.getTotalAmount(), requestEvent.getOrderLineItems());

    }
}
