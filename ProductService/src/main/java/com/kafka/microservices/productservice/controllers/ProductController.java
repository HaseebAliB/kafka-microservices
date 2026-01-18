package com.kafka.microservices.productservice.controllers;

import com.kafka.microservices.common.ProductEvent;
import com.kafka.microservices.productservice.model.Product;
import com.kafka.microservices.productservice.model.ProductBuyDto;
import com.kafka.microservices.productservice.services.CachingService;
import com.kafka.microservices.productservice.services.MessageService;
import com.kafka.microservices.productservice.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

private final MessageService messageService;
private final ProductService productService;
private final CachingService cachingService;
private final int SCROLL_LIMIT = 50;
    @GetMapping
    public Map<String, Object> getProducts(
            @RequestParam(name="page",defaultValue = "0") int page,
            @RequestParam(name="size",defaultValue = "50") int size
    ) {
        long start = System.currentTimeMillis();


        long end = System.currentTimeMillis();

        Map<String, Object> response = new HashMap<>();
        response.put("products", cachingService.getPaginated(page,size));
        response.put("backendTime", end - start);
        response.put("scrollLimit", SCROLL_LIMIT);
        response.put("totalItems", cachingService.getTotalCount());
        return response;
    }

@PostMapping("/create")
   public ResponseEntity<?> create(@RequestBody ProductEvent productEvent){
    try{
        String productId = messageService.publish(productEvent);
        return ResponseEntity.ok(productId);
    }catch (Exception e){
        return ResponseEntity.status(500).body("Error occurred while publishing product: " + e.getMessage());
    }

}

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getOne(@PathVariable("id") Long id){
        Product product = productService.findById(id);
        if (product == null) {
            return ResponseEntity.status(500).body("Product not found");
        } else {
            return ResponseEntity.ok(product);
        }
    }

    @PostMapping("/buy")
    public ResponseEntity<?> buy(@RequestBody ProductBuyDto productBuyDto){
        try{
            return ResponseEntity.ok(productService.placeProductOrder(productBuyDto));
        }catch (Exception ex){
            return ResponseEntity.status(500).body("Error occurred while placing order: " + ex.getMessage());
        }

    }

}
