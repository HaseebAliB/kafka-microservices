package com.kafka.microservices.productservice.services;

import com.kafka.microservices.common.OrderCompleteEvent;
import com.kafka.microservices.common.OrderLineItemDto;
import com.kafka.microservices.common.OrderRequestEvent;
import com.kafka.microservices.productservice.model.Product;
import com.kafka.microservices.productservice.model.ProductBuyDto;

import java.util.List;

public interface ProductService {

    Boolean productsAvailable(List<OrderLineItemDto> lineItems);

    void updateQuantity(OrderCompleteEvent orderCompleteEvent);

    OrderRequestEvent placeProductOrder(ProductBuyDto productBuyDto) throws  Exception;

    Product findById(Long id);

    void bootstrapProducts(List<Product> products);

}
