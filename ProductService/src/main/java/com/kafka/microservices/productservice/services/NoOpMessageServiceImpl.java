package com.kafka.microservices.productservice.services;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("default")
public class NoOpMessageServiceImpl implements MessageService {
    @Override
    public String publish(com.kafka.microservices.common.ProductEvent productCreationEvent) throws Exception {
        return null;
    }

    @Override
    public String placeOrderRequest(com.kafka.microservices.common.OrderRequestEvent orderRequestEvent) throws Exception {
        return null;
    }
}
