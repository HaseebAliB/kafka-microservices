package com.kafka.microservices.wsgateway.services;

import com.kafka.microservices.common.OrderCompleteEvent;
import com.kafka.microservices.common.Topics;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service

public class OrderStatusPublisher {

    private final SimpMessagingTemplate messagingTemplate;

    public OrderStatusPublisher(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @KafkaListener(
            topics = Topics.ORDER_COMPLETE_TOPIC,
            groupId = "ws-gateway-group"
    )
    public void publish(OrderCompleteEvent event) {

        messagingTemplate.convertAndSend(
                "/topic/orders/" + event.getOrderId(),
                event
        );
    }
}
