package com.kafka.microservices.productservice.services;

import com.kafka.microservices.common.OrderRequestEvent;
import com.kafka.microservices.common.ProductEvent;
import com.kafka.microservices.common.Topics;
import com.kafka.microservices.productservice.model.Product;
import com.kafka.microservices.productservice.model.ProductRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile("k8s")
public class MessageServiceImpl implements  MessageService{

private final KafkaTemplate<String, Object>  kafkaTemplate;
private final ProductRepository productRepository;

@Transactional(value = "transactionManager")
public String publish(ProductEvent productCreationEvent) throws Exception{

    log.info("******inserting into ProductRepo from ProductCreatedEvent");
    Product product = Product.builder()
            .sku(UUID.randomUUID().toString())
            .name(productCreationEvent.getName())
            .price(productCreationEvent.getPrice())
            .stockQuantity(productCreationEvent.getQuantity())
            .build();

    Product newProduct = productRepository.save(product);

    log.info("******inserted into ProductRepo");

    ProducerRecord<String, Object> record = new ProducerRecord<>(
            Topics.PRODUCT_CREATION_TOPIC,
            newProduct.getSku(),
            productCreationEvent);
    record.headers().add("messageId", UUID.randomUUID().toString().getBytes());

    log.info("******Before publishing a ProductCreatedEvent");
    SendResult<String, Object> result = kafkaTemplate.send(record).get();
    log.info("******Partition: " + result.getRecordMetadata().partition());
    log.info("******Topic: " + result.getRecordMetadata().topic());
    log.info("******Offset: " + result.getRecordMetadata().offset());

 return newProduct.getSku();

   // if (true) throw new RuntimeException("Simulated exception for testing error handling");

}
@Transactional(value = "transactionManager")
public String placeOrderRequest(OrderRequestEvent orderRequestEvent) throws Exception{
    ProducerRecord<String, Object> record = new ProducerRecord<>(
            Topics.ORDER_REQUEST_TOPIC,
            orderRequestEvent.getOrderId(),
            orderRequestEvent);

    log.info("******Before publishing a OrderRequestEvent");
    SendResult<String, Object> result = kafkaTemplate.send(record).get();
    log.info("******Partition: " + result.getRecordMetadata().partition());
    log.info("******Topic: " + result.getRecordMetadata().topic());
    log.info("******Offset: " + result.getRecordMetadata().offset());

    return record.key();

}



}
