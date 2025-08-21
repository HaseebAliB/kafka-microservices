package com.kafka.microservices.orderservice.handlers;

import com.kafka.microservices.common.*;
import com.kafka.microservices.orderservice.model.Order;
import com.kafka.microservices.orderservice.model.OrderStatus;
import com.kafka.microservices.orderservice.services.OrderHistoryService;
import com.kafka.microservices.orderservice.services.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@KafkaListener(topics = {Topics.PRODUCT_RESERVE_RESPONSE_TOPIC,Topics.PAYMENT_REQUEST_TOPIC
                         ,Topics.PAYMENT_RESPONSE_TOPIC,Topics.ORDER_UPDATE_TOPIC})
@Component
@RequiredArgsConstructor
@Slf4j
public class OrderSaga {

    private final OrderHistoryService orderHistoryService;
    private final OrderService orderService;
    private final KafkaTemplate kafkaTemplate;

    @KafkaHandler
    @Transactional("transactionManager")
    public void handleProductReserveResponseEvent(@Payload ProductReservedEvent productReservedEvent, @Header(KafkaHeaders.RECEIVED_KEY) String messageKey) {
        log.info("**** Handling ProductReservedEvent in OrderSaga ****");
        Order order = orderService.findById(messageKey);
        if (order == null) {
            log.error("Order not found for ID: " + messageKey);
            return;
        }
        Order freshOrder = orderService.updateOrder(messageKey, productReservedEvent.getOrderLineItems());

        orderHistoryService.createOrderHistory(order, OrderStatus.VALIDATED);

        log.info("**** Created OrderHistory from  OrderSaga **** OrderId::  " + order.getId() + " status:: " + OrderStatus.VALIDATED);

        PaymentRequestEvent requestEvent = new PaymentRequestEvent();
        requestEvent.setTotalAmount(freshOrder.getAmount());
        requestEvent.setOrderLineItems(productReservedEvent.getOrderLineItems());
        ProducerRecord<String, Object> producerRecord = new ProducerRecord<>(Topics.PAYMENT_REQUEST_TOPIC, messageKey, requestEvent);
        kafkaTemplate.send(producerRecord);

        log.info("**** Pushed in topic from  OrderSaga ****  " + Topics.PAYMENT_REQUEST_TOPIC);
    }


    @KafkaHandler
    @Transactional("transactionManager")
    public void handlePaymentProcessedEvent(@Payload PaymentProcessedEvent paymentProcessedEvent,
                                            @Header(KafkaHeaders.RECEIVED_KEY) String messageKey) {
        log.info("**** Handling PaymentProcessedEvent in OrderSaga ***");
        Order order = orderService.findById(messageKey);
        orderHistoryService.createOrderHistory(order, OrderStatus.APPROVED);
        orderService.updateOrder(messageKey, OrderStatus.APPROVED);
        log.info("**** Order completed from OrderSaga ***");
    }

    @KafkaHandler
    @Transactional("transactionManager")
    public void handleProductReserveFailedEvent(@Payload ProductReserveFailedEvent productReserveFailedEvent,
                                                @Header(KafkaHeaders.RECEIVED_KEY) String messageKey) {

        log.info("**** Handling ProductReserveFailedEvent in OrderSaga ***");
        Order order = orderService.findById(messageKey);
        orderHistoryService.createOrderHistory(order, OrderStatus.REJECTED);
        orderService.updateOrder(messageKey, OrderStatus.REJECTED);
        log.info("**** Order cancelled from OrderSaga ***");

    }

    @KafkaHandler
    @Transactional("transactionManager")
    public void handlePaymentFailedEvent(@Payload PaymentFailedEvent paymentFailedEvent,
                                         @Header(KafkaHeaders.RECEIVED_KEY) String messageKey) {

        log.info("**** Handling PaymentFailedEvent in OrderSaga ***");
        ProductReserveCancelEvent productReserveCancelEvent = new ProductReserveCancelEvent();
        productReserveCancelEvent.setOrderLineItems(paymentFailedEvent.getOrderLineItems());
        ProducerRecord<String, Object> producerRecord = new ProducerRecord<>(Topics.PRODUCT_RESERVE_RESPONSE_TOPIC, messageKey,productReserveCancelEvent);
        kafkaTemplate.send(producerRecord);
        log.info("**** Pushed in topic from  OrderSaga ****  " + Topics.PRODUCT_RESERVE_RESPONSE_TOPIC);


    }


    @KafkaHandler
    @Transactional("transactionManager")
    public void handleOrderCancelledEvent(@Payload OrderCancelledEvent cancelEvent,
                                                @Header(KafkaHeaders.RECEIVED_KEY) String messageKey) {

        log.info("**** Handling OrderCancelledEvent in OrderSaga ***");
        Order order = orderService.findById(messageKey);
        orderHistoryService.createOrderHistory(order, OrderStatus.REJECTED);
        orderService.updateOrder(messageKey, OrderStatus.REJECTED);
        log.info("**** Order cancelled from OrderSaga ***");

    }

}