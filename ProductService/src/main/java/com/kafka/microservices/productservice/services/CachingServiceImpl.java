package com.kafka.microservices.productservice.services;

import com.kafka.microservices.productservice.mapper.ProductHashMapper;
import com.kafka.microservices.productservice.model.Product;
import com.kafka.microservices.productservice.model.ProductDto;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Profile("k8s")
public class CachingServiceImpl implements CachingService{
private final RedisTemplate<String, Object> redisTemplate;
private final ProductHashMapper productHashMapper;
    private static final String PRODUCT_KEY_PREFIX = "product:";
    private static final String PRODUCT_INDEX = "product:index";
    private static final String PRODUCT_COUNT = "product:count";
    public void saveProduct(Product product) {
        String productKey = PRODUCT_KEY_PREFIX + product.getId();
        ProductDto productDto = productHashMapper.toProductDto(product);
        Map<String,Object> map = productHashMapper.toMap(productDto);
        redisTemplate.opsForHash().putAll(productKey, map);
        //redisTemplate.opsForHash().delete("product:1", "oldColumn", "anotherOldField"); delete specific columns if schema is changed
         Boolean added = redisTemplate.opsForZSet().add(PRODUCT_INDEX, product.getId().toString(),product.getRating());

        // Increase count only if ID was newly added
        if (Boolean.TRUE.equals(added)) {
            redisTemplate.opsForValue().increment(PRODUCT_COUNT);
        }
    }
    public ProductDto findById(Long id) {
        String productKey = PRODUCT_KEY_PREFIX + id;
        Map<Object, Object> map = redisTemplate.opsForHash().entries(productKey);
        if (map == null || map.isEmpty()) {
            return null;
        }
        return productHashMapper.fromMap(map, ProductDto.class);
    }

    public List<ProductDto> getPaginated(int page, int size){
        long start = (long) (page - 1) * size;
        long end = start + size - 1;

        var productIds = redisTemplate.opsForZSet().reverseRange(PRODUCT_INDEX, start, end);
        if(productIds == null || productIds.isEmpty()){
            return List.of();
        }
        return productIds.stream()
                .map(id -> findById(Long.parseLong(id.toString())))
                .toList();
    }

    public long getTotalCount() {
        Object value = redisTemplate.opsForValue().get(PRODUCT_COUNT);
        if (value == null) return 0;
        return Long.parseLong(value.toString());
    }

    public void updateProductQuantity(Long productId, Integer quantity) {
        String productKey = PRODUCT_KEY_PREFIX + productId;
        redisTemplate.opsForHash().put(productKey, "stockQuantity", quantity);
    }
}
