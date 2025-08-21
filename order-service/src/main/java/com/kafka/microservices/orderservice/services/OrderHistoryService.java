package com.kafka.microservices.orderservice.services;

import com.kafka.microservices.orderservice.dto.OrderHistoryDto;
import com.kafka.microservices.orderservice.model.Order;
import com.kafka.microservices.orderservice.model.OrderHistory;
import com.kafka.microservices.orderservice.model.OrderStatus;

import java.util.List;

public interface OrderHistoryService {
    void createOrderHistory(Order order, OrderStatus orderStatus);

    List<OrderHistoryDto> getByOrderId(String orderId);
}
