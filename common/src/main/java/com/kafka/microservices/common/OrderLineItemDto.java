package com.kafka.microservices.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderLineItemDto {
    private String productId;
    private String orderId;
    private Double price;
    private Integer quantity;
}
