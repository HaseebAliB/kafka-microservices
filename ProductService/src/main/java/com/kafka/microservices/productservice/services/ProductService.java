package com.kafka.microservices.productservice.services;

import com.kafka.microservices.common.OrderLineItemDto;

import java.util.List;

public interface ProductService {

    Boolean productsAvailable(List<OrderLineItemDto> lineItems);
    void reserveProducts(List<OrderLineItemDto> lineItems, String orderId);

    void cancelProductReservation(List<OrderLineItemDto> lineItems, String orderId) throws  Exception;

}
