package com.kafka.microservices.orderservice.services;

import com.kafka.microservices.common.OrderLineItemDto;
import com.kafka.microservices.orderservice.dto.OrderDto;
import com.kafka.microservices.orderservice.model.Order;
import com.kafka.microservices.orderservice.model.OrderStatus;

import java.util.List;

public interface OrderService {
    OrderDto placeOrder(OrderDto orderDto);

    void updateOrder(String orderId, OrderStatus orderStatus);
    Order updateOrder(String orderId,List<OrderLineItemDto> lineItems);
    Order findById(String orderId);

    void CancelOrder(String orderId);



}
