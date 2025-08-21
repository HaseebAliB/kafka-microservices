package com.kafka.microservices.paymentservice.services;

import com.kafka.microservices.common.OrderLineItem;

import java.util.List;

public interface PaymentService {

    void processPayment(String orderId, Double amount, List<OrderLineItem> lineItemList);

}


