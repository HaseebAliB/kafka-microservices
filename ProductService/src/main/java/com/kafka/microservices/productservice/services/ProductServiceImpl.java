package com.kafka.microservices.productservice.services;

import com.kafka.microservices.common.OrderCompleteEvent;
import com.kafka.microservices.common.OrderLineItemDto;
import com.kafka.microservices.common.OrderRequestEvent;
import com.kafka.microservices.productservice.model.Product;
import com.kafka.microservices.productservice.model.ProductBuyDto;
import com.kafka.microservices.productservice.model.ProductDto;
import com.kafka.microservices.productservice.model.ProductRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {
private final ProductRepository productRepository;
private final MessageService messageService;
private final CachingService cachingService;
private final EntityManager entityManager;


    @Override
    public Boolean productsAvailable(List<OrderLineItemDto> lineItems) {
        for (OrderLineItemDto lineItem : lineItems){

            Optional<Product> product = productRepository.findBySku(lineItem.getProductId());
            if (product.isEmpty())
                return false;
         if (lineItem.getQuantity() > product.get().getStockQuantity() ) {
                return false; // Not enough quantity available
            }
        }

        return true;
    }



    @Override
    @Transactional("transactionManager")
    public void updateQuantity(OrderCompleteEvent orderCompleteEvent) {
        Optional<Product> product = productRepository.findById(orderCompleteEvent.getProductId());
        if (product.isPresent()){
            Product currentProduct =product.get();
           Integer finalQty = currentProduct.getStockQuantity() - orderCompleteEvent.getQuantity();
            currentProduct.setStockQuantity(finalQty);
            productRepository.save(currentProduct);
            cachingService.updateProductQuantity(currentProduct.getId(),finalQty);
        }else{
            log.info("**** Product not found for id:: "+ orderCompleteEvent.getProductId());
        }
    }

    @Override
    @Transactional("transactionManager")
    public OrderRequestEvent placeProductOrder(ProductBuyDto productBuyDto) throws Exception{
        ProductDto productDto = cachingService.findById(productBuyDto.getProductId());
        OrderRequestEvent orderRequestEvent = OrderRequestEvent.builder()
                .orderId(UUID.randomUUID().toString())
                .quantity(productBuyDto.getQuantity())
                .price(productDto.getPrice())
                .productId(productDto.getId())
                .build();
        messageService.placeOrderRequest(orderRequestEvent);
        return orderRequestEvent;
    }

    @Override
    public Product findById(Long id) {
        Optional<Product> productObject = productRepository.findById(id);
        if (productObject.isPresent()){
            return productObject.get();
        }
        return null;
    }

    @Override
    @Transactional("transactionManager")
    public void bootstrapProducts(List<Product> products) {
        int batchSize = 1000;
        int i = 0;
        for (Product product : products) {
            entityManager.persist(product);
            i++;
            if (i % batchSize == 0) {
                entityManager.flush();
                entityManager.clear();
            }
        }
        entityManager.flush();
        entityManager.clear();
    }


}
