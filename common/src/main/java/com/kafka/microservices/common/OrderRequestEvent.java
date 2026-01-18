package com.kafka.microservices.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderRequestEvent {
    private Long productId;
    private String orderId;
    private Double price;
    private Integer quantity;
}
