package com.kafka.microservices.productservice.services;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("default")
public class NoOpCachingserviceImpl implements CachingService {
    @Override
    public void saveProduct(com.kafka.microservices.productservice.model.Product product) {
        // No operation
    }

    @Override
    public com.kafka.microservices.productservice.model.ProductDto findById(Long id) {
        return null;
    }

    @Override
    public java.util.List<com.kafka.microservices.productservice.model.ProductDto> getPaginated(int page, int size) {
        return null;
    }

    @Override
    public long getTotalCount() {
        return 0;
    }

    @Override
    public void updateProductQuantity(Long productId, Integer quantity) {

    }
}
