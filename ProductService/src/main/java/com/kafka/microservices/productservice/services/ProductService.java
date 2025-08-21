package com.kafka.microservices.productservice.services;

import com.kafka.microservices.common.OrderLineItem;

import java.util.List;

public interface ProductService {

    Boolean productsAvailable(List<OrderLineItem> lineItems);
    void reserveProducts(List<OrderLineItem> lineItems,String orderId);

}
