package com.kafka.microservices.paymentservice.services;

import com.kafka.microservices.common.PaymentProcessedEvent;
import com.kafka.microservices.common.PaymentRequestEvent;
import com.kafka.microservices.common.Topics;
import com.kafka.microservices.paymentservice.model.Payment;
import com.kafka.microservices.paymentservice.model.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl  implements  PaymentService{
    private final PaymentRepository paymentRepository;
    private final KafkaTemplate kafkaTemplate;

    @Override
    @Transactional("transactionManager")
    public void processPayment(PaymentRequestEvent paymentRequestEvent) {

       Double amount = paymentRequestEvent.getPrice() * paymentRequestEvent.getQuantity();

        Payment payment  = Payment.builder().processed(true).amount(amount)
                .orderId(paymentRequestEvent.getOrderId())
                .id(UUID.randomUUID().toString()).build();

        paymentRepository.save(payment);
        PaymentProcessedEvent responseEvent =PaymentProcessedEvent.builder()
                .orderId(paymentRequestEvent.getOrderId())
                .quantity(paymentRequestEvent.getQuantity())
                .price(paymentRequestEvent.getPrice())
                .productId(paymentRequestEvent.getProductId())
                .build();

        ProducerRecord<String,Object> producerRecord = new
                ProducerRecord<>(Topics.PAYMENT_PROCESSED_TOPIC,paymentRequestEvent.getOrderId()
                ,responseEvent);
        kafkaTemplate.send(producerRecord);

    }
}
