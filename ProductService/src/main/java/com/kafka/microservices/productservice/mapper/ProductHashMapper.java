package com.kafka.microservices.productservice.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kafka.microservices.productservice.model.Product;
import com.kafka.microservices.productservice.model.ProductDto;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ProductHashMapper {

    private final ObjectMapper mapper = new ObjectMapper();

    public Map<String, Object> toMap(Object object) {
        return mapper.convertValue(object, Map.class);
    }

    public <T> T fromMap(Map<Object, Object> map, Class<T> clazz) {
        return mapper.convertValue(map, clazz);
    }

    public ProductDto toProductDto(Product product){
        if (product == null) return null;

        return ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .rating(product.getRating())
                .category(product.getCategory())
                .stockQuantity(product.getStockQuantity())
                .price(product.getPrice())
                .build();
    }





}
