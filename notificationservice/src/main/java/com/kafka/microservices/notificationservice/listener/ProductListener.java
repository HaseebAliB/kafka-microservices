package com.kafka.microservices.notificationservice.listener;


import com.kafka.microservices.common.ProductEvent;
import com.kafka.microservices.common.Topics;
import com.kafka.microservices.notificationservice.exception.NotRetryableException;
import com.kafka.microservices.notificationservice.model.ProcessedEvent;
import com.kafka.microservices.notificationservice.model.ProcessedEventRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(topics = Topics.PRODUCT_CREATION_TOPIC)
@Slf4j
@RequiredArgsConstructor
public class ProductListener {

    private final ProcessedEventRepository processedEventRepository;

    @KafkaHandler
    @Transactional
    public void handle(@Payload ProductEvent productCreatedEvent, @Header("messageId") String messageId) {
        log.info("Received new ProductEvent : " + productCreatedEvent.getProductId());

        if( processedEventRepository.findByMessageId(messageId)!= null){
        log.info("Message already processed.. " + productCreatedEvent.getProductId());
        return;
        }

        try {
            processedEventRepository.save(ProcessedEvent.builder().messageId(messageId).productId(productCreatedEvent.getProductId()).build());
            log.info("Processed new ProductEvent : " + productCreatedEvent.getProductId());
        } catch (DataIntegrityViolationException ex) {
            throw new NotRetryableException(ex);
        }

    }
}
