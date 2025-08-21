package com.kafka.microservices.orderservice.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;

@Entity
@Table(name = "hb_orderHistory")
@Data
public class OrderHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    private LocalDate updatedAt;

public void addOrder(Order order, OrderStatus orderStatus){
    this.order = order;
    this.orderStatus = orderStatus;
}



}
