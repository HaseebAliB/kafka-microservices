package com.kafka.microservices.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderCancelledEvent {
    private List<OrderLineItemDto> orderLineItems;
    private String reason;
}
