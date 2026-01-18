package com.kafka.microservices.productservice.eventhandlers;


import com.kafka.microservices.common.OrderCompleteEvent;
import com.kafka.microservices.common.Topics;
import com.kafka.microservices.productservice.services.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


@Slf4j
@RequiredArgsConstructor
@Component
public class ProductEventHandler {
    private final ProductService productService;


    @KafkaListener(
            topics = Topics.ORDER_COMPLETE_TOPIC,
            groupId = "product-service-group")
    @Profile("k8s")
    public void handleOrderCompleteEvent(OrderCompleteEvent orderCompleteEvent) throws Exception {
        log.info("**** Handling OrderCompleteEvent in ProductEventHandler ****");
        productService.updateQuantity(orderCompleteEvent);
        log.info("**** Completed handling OrderCompleteEvent in ProductEventHandler ****");

    }

}
