package com.kafka.microservices.orderservice.services;

import com.kafka.microservices.orderservice.dto.OrderHistoryDto;
import com.kafka.microservices.orderservice.model.Order;
import com.kafka.microservices.orderservice.model.OrderHistory;
import com.kafka.microservices.orderservice.model.OrderHistoryRepository;
import com.kafka.microservices.orderservice.model.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderHistoryServiceImpl implements  OrderHistoryService {

    private final OrderHistoryRepository orderHistoryRepository;

    @Override
    public List<OrderHistoryDto> getByOrderId(String orderId) {
        List<OrderHistory> orderHistory =  orderHistoryRepository.findByOrderId(orderId);
        if (orderHistory.isEmpty()) {
            throw new RuntimeException("No order history found for orderId: " + orderId);
        }

        return fromOrderHistory(orderHistory);
    }

    private List<OrderHistoryDto> fromOrderHistory(List<OrderHistory> orderHistoryList){
        List<OrderHistoryDto> historyList = orderHistoryList.stream()
                .map(dto -> {OrderHistoryDto history = new OrderHistoryDto();
                       history.setOrderId(dto.getOrder().getId());
                          history.setOrderStatus(dto.getOrderStatus().name());
                          history.setUpdatedAt(dto.getUpdatedAt());
                          return history;
                } )
                .toList();
        return historyList;
    }
    @Override
   // @Transactional(propagation = Propagation.MANDATORY)
    public void createOrderHistory(Order order, OrderStatus orderStatus) {
        OrderHistory orderHistory = new OrderHistory();
        orderHistory.setUpdatedAt(LocalDate.now());
        orderHistory.addOrder(order, orderStatus);
        orderHistoryRepository.save(orderHistory);
    }
}
