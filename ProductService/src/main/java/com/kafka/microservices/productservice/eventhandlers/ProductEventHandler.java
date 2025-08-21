package com.kafka.microservices.productservice.eventhandlers;


import com.kafka.microservices.common.ProductReserveRequestEvent;
import com.kafka.microservices.common.Topics;
import com.kafka.microservices.productservice.services.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@KafkaListener(topics = {Topics.PRODUCT_RESERVE_REQUEST_TOPIC})
@Slf4j
@RequiredArgsConstructor
@Component
public class ProductEventHandler {
private final ProductService productService;



 @KafkaHandler
    public void handleProductReserveRequest(@Payload  ProductReserveRequestEvent requestEvent, @Header(KafkaHeaders.RECEIVED_KEY) String messageKey) {
     log.info("Handling ProductReserveRequestEvent");
     productService.reserveProducts(requestEvent.getOrderLineItems(), messageKey);
    }


}
