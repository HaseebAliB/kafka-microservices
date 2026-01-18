package com.kafka.microservices.orderservice.handlers;

import com.kafka.microservices.common.*;
import com.kafka.microservices.orderservice.model.Order;
import com.kafka.microservices.orderservice.model.OrderStatus;
import com.kafka.microservices.orderservice.services.OrderHistoryService;
import com.kafka.microservices.orderservice.services.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/*@KafkaListener(topics = {Topics.PRODUCT_RESERVE_RESPONSE_TOPIC,Topics.PAYMENT_REQUEST_TOPIC
                         ,Topics.PAYMENT_RESPONSE_TOPIC,Topics.ORDER_UPDATE_TOPIC,Topics.ORDER_REQUEST_TOPIC})*/
@Component
@RequiredArgsConstructor
@Slf4j
public class OrderSaga {

    private final OrderHistoryService orderHistoryService;
    private final OrderService orderService;
    private final KafkaTemplate kafkaTemplate;

  /*  @KafkaHandler
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
*/
    @KafkaListener(topics = Topics.ORDER_REQUEST_TOPIC,groupId = "order-saga-group")
    @Transactional("transactionManager")
    public void handleOrderRequestEvent(OrderRequestEvent orderRequestEvent){
        log.info("**** Handling OrderRequest Event ***");
        Order order = Order.builder()
                .id(orderRequestEvent.getOrderId())
                .amount(orderRequestEvent.getPrice() * orderRequestEvent.getQuantity())
                .status(OrderStatus.PROCESSING)
                .creationDate(LocalDate.now())
                .build();

        orderService.saveOrder(order);

        log.info("**** Order saved in repo ***" + order.getId());

        PaymentRequestEvent paymentRequestEvent = PaymentRequestEvent.builder().orderId(orderRequestEvent.getOrderId())
                .productId(orderRequestEvent.getProductId())
                .price(orderRequestEvent.getPrice())
                .quantity(orderRequestEvent.getQuantity())
                .build();

        kafkaTemplate.send(Topics.PAYMENT_REQUEST_TOPIC,orderRequestEvent.getOrderId()
        ,paymentRequestEvent );

        log.info("**** sent Payment Request Event ***" + order.getId());

    }


    @KafkaListener(topics = Topics.PAYMENT_PROCESSED_TOPIC,groupId = "order-saga-group")
    @Transactional("transactionManager")
    public void handlePaymentProcessedEvent(PaymentProcessedEvent paymentProcessedEvent){
        log.info("***Handling PaymentProcessedEvent in OrderSaga ***");
        Order order = orderService.findById(paymentProcessedEvent.getOrderId());
        order.setStatus(OrderStatus.COMPLETED);
        orderService.saveOrder(order);
        log.info("**** Order completed from OrderSaga ***" + order.getId());

        OrderCompleteEvent orderCompleteEvent = OrderCompleteEvent.builder()
                .orderId(order.getId())
                .productId(paymentProcessedEvent.getProductId())
                .price(paymentProcessedEvent.getPrice())
                .quantity(paymentProcessedEvent.getQuantity())
                .status(OrderStatus.COMPLETED.name())
                .build();

        kafkaTemplate.send(Topics.ORDER_COMPLETE_TOPIC,order.getId(),orderCompleteEvent);

        log.info("**** sent OrderCompleteEvent from OrderSaga ***" + order.getId());
    }

}