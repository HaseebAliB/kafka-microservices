package com.kafka.microservices.orderservice.controller;

import com.kafka.microservices.orderservice.dto.OrderDto;
import com.kafka.microservices.orderservice.services.OrderHistoryService;
import com.kafka.microservices.orderservice.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/order")
@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderHistoryService orderHistoryService;


@PostMapping("/create")
    public ResponseEntity<?> placeOrder(@RequestBody OrderDto orderDto){
     try{
          OrderDto response = orderService.placeOrder(orderDto);
         return ResponseEntity.ok(response);
     }catch(Exception ex){
         return ResponseEntity.status(500).build();
     }
}


@GetMapping("history/{orderId}")
    public ResponseEntity<?> getOrderHistory(@PathVariable String orderId){
        try{
            return ResponseEntity.ok(orderHistoryService.getByOrderId(orderId));
        }catch(Exception ex){
            return ResponseEntity.status(500).build();
        }
    }
}
