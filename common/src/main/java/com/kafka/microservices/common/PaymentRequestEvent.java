package com.kafka.microservices.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class PaymentRequestEvent {
    List<OrderLineItemDto> orderLineItems;
    private Double totalAmount;

}
