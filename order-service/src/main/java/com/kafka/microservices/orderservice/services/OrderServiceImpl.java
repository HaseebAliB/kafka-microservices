package com.kafka.microservices.orderservice.services;

import com.kafka.microservices.common.ProductReserveRequestEvent;
import com.kafka.microservices.common.Topics;
import com.kafka.microservices.orderservice.dto.OrderDto;
import com.kafka.microservices.orderservice.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderServiceImpl implements  OrderService{

    private final OrderRepository orderRepository;
    private final OrderHistoryService orderHistoryService;
    private final KafkaTemplate kafkaTemplate;

    @Override
    public Order findById(String orderId) {
        return orderRepository.findById(orderId).get();
    }

    @Override
    public Order updateOrder(String orderId, List<com.kafka.microservices.common.OrderLineItem> lineItems) {
        Order order = findById(orderId);
        order.setAmount(calculateOrderAmount(lineItems));
        return orderRepository.save(order);
    }

    @Override
    @Transactional("transactionManager")
    public OrderDto placeOrder(OrderDto orderDto) {
        log.info("******** Placing Order *******");


        List<OrderLineItem> lineItems = orderDto.getLineItems().stream()
                .map(dto -> OrderLineItem.builder()
                            .productId(dto.getProductId())
                            .quantity(dto.getQuantity())
                            .build())
                .toList();

        Order newOrder = Order.builder().id(UUID.randomUUID().toString())
                .orderLineItems(lineItems)
                .status(OrderStatus.CREATED)
                .build();

        Order freshOrder = orderRepository.save(newOrder);

        log.info("******** Order saved in DB  OrderId:: ******* " + freshOrder.getId());

       orderHistoryService.createOrderHistory(freshOrder,OrderStatus.CREATED);

        log.info("******** Order History Created OrderId:: ******* " + freshOrder.getId());

            orderDto.setId(freshOrder.getId());


            ProductReserveRequestEvent requestEvent = new ProductReserveRequestEvent();
            requestEvent.setOrderLineItems(orderDto.getLineItems());

            ProducerRecord<String, Object> producerRecord = new ProducerRecord<>(Topics.PRODUCT_RESERVE_REQUEST_TOPIC, freshOrder.getId(), requestEvent);
            producerRecord.headers().add("messageId", UUID.randomUUID().toString().getBytes());
            kafkaTemplate.send(producerRecord);

            log.info("******** Order pushed in  " + Topics.PRODUCT_RESERVE_REQUEST_TOPIC +
                    " OrderID:: ******* " + freshOrder.getId());

        return orderDto;
        }

    @Override
    public void updateOrder(String orderId, OrderStatus orderStatus) {
        Order order = findById(orderId);
        order.setStatus(orderStatus);
        orderRepository.save(order);
    }



    private Double calculateOrderAmount(List<com.kafka.microservices.common.OrderLineItem> lineItems){
        Double amount = lineItems.stream().map(
                item -> item.getPrice() * item.getQuantity()
        ).reduce(0.0, Double::sum);

        return amount;
    }
}
