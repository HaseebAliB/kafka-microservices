package com.kafka.microservices.productservice.controllers;

import com.kafka.microservices.common.ProductEvent;
import com.kafka.microservices.productservice.services.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

private final MessageService messageService;


@PostMapping("/create")
   public ResponseEntity<?> create(@RequestBody ProductEvent productEvent){
    try{
        messageService.publish(productEvent);
    }catch (Exception e){
        return ResponseEntity.status(500).body("Error occurred while publishing product: " + e.getMessage());
    }
    return ResponseEntity.ok("Product created successfully");
}


}
