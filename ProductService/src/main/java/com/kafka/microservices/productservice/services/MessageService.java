package com.kafka.microservices.productservice.services;

import com.kafka.microservices.common.OrderRequestEvent;
import com.kafka.microservices.common.ProductEvent;

public interface MessageService {
   String publish(ProductEvent productCreationEvent) throws Exception;
   String placeOrderRequest(OrderRequestEvent orderRequestEvent) throws Exception;
}
