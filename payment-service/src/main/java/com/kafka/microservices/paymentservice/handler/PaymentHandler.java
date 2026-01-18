package com.kafka.microservices.paymentservice.handler;

import com.kafka.microservices.common.PaymentRequestEvent;
import com.kafka.microservices.common.Topics;
import com.kafka.microservices.paymentservice.services.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentHandler {
    private final PaymentService paymentService;

    @KafkaListener(
            topics = Topics.PAYMENT_REQUEST_TOPIC,
            groupId = "payment-service-group"
          )
    public void handlePaymentRequest(PaymentRequestEvent requestEvent ) {
        log.info("**** Handling PaymentRequestEvent in PaymentHandler ****");
        paymentService.processPayment(requestEvent);
        log.info("**** PaymentProcessed Event sent ****");
    }
}
