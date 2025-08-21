package com.kafka.microservices.orderservice.dto;

import lombok.Data;

import java.time.LocalDate;
@Data
public class OrderHistoryDto {
    private String orderId;
    private String orderStatus;
    private LocalDate updatedAt;

}
