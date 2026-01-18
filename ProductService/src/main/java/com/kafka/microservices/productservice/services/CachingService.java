package com.kafka.microservices.productservice.services;

import com.kafka.microservices.productservice.model.Product;
import com.kafka.microservices.productservice.model.ProductDto;

import java.util.List;

public interface CachingService {
    void saveProduct(Product product);
    ProductDto findById(Long id);
    List<ProductDto> getPaginated(int page, int size);
    long getTotalCount();
    void updateProductQuantity(Long productId, Integer quantity);
}
