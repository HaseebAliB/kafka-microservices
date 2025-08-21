package com.kafka.microservices.paymentservice.services;

import com.kafka.microservices.common.OrderLineItem;
import com.kafka.microservices.common.PaymentProcessedEvent;
import com.kafka.microservices.common.Topics;
import com.kafka.microservices.paymentservice.model.Payment;
import com.kafka.microservices.paymentservice.model.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl  implements  PaymentService{
    private final PaymentRepository paymentRepository;
    private final KafkaTemplate kafkaTemplate;

    @Override
    public void processPayment(String orderId, Double amount, List<OrderLineItem> itemList) {
        Payment payment  = Payment.builder().processed(true).amount(amount)
                .orderId(orderId)
                .id(UUID.randomUUID().toString()).build();

        paymentRepository.save(payment);
        PaymentProcessedEvent responseEvent = new PaymentProcessedEvent();
        responseEvent.setOrderLineItems(itemList);
        ProducerRecord<String,Object> producerRecord = new
                ProducerRecord<>(Topics.PAYMENT_RESPONSE_TOPIC,orderId,responseEvent);
        kafkaTemplate.send(producerRecord);

    }
}
