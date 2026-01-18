package com.kafka.microservices.orderservice.model;

public enum OrderStatus {
    CREATED,
    VALIDATED,
    APPROVED,
    REJECTED,

    PROCESSING,

    COMPLETED
}
