package com.kafka.microservices.productservice.services;

import com.kafka.microservices.common.ProductEvent;
import com.kafka.microservices.common.Topics;
import com.kafka.microservices.productservice.model.Product;
import com.kafka.microservices.productservice.model.ProductRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class MessageService {

private final KafkaTemplate<String, Object>  kafkaTemplate;
private final ProductRepository productRepository;

@Transactional(value = "transactionManager")
public String publish(ProductEvent productCreationEvent) throws Exception{

    log.info("******inserting into ProductRepo from ProductCreatedEvent");
    Product product = Product.builder()
            .productId(UUID.randomUUID().toString())
            .name(productCreationEvent.getName())
            .price(productCreationEvent.getPrice())
            .quantity(productCreationEvent.getQuantity())
            .build();

    Product newProduct = productRepository.save(product);

    log.info("******inserted into ProductRepo");

    ProducerRecord<String, Object> record = new ProducerRecord<>(
            Topics.PRODUCT_CREATION_TOPIC,
            newProduct.getProductId(),
            productCreationEvent);
    record.headers().add("messageId", UUID.randomUUID().toString().getBytes());

    log.info("******Before publishing a ProductCreatedEvent");
    SendResult<String, Object> result = kafkaTemplate.send(record).get();
    log.info("******Partition: " + result.getRecordMetadata().partition());
    log.info("******Topic: " + result.getRecordMetadata().topic());
    log.info("******Offset: " + result.getRecordMetadata().offset());

 return newProduct.getProductId();

   // if (true) throw new RuntimeException("Simulated exception for testing error handling");



}


}
