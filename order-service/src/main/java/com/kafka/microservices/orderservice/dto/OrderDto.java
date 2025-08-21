package com.kafka.microservices.orderservice.dto;

import com.kafka.microservices.common.OrderLineItemDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDto {
    private String id;
    private Double amount = 0.0;
    private List<OrderLineItemDto> lineItems;
}
