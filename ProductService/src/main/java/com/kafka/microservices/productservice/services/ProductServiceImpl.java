package com.kafka.microservices.productservice.services;

import com.kafka.microservices.common.OrderLineItem;
import com.kafka.microservices.common.ProductReserveFailedEvent;
import com.kafka.microservices.common.ProductReservedEvent;
import com.kafka.microservices.common.Topics;
import com.kafka.microservices.productservice.model.Product;
import com.kafka.microservices.productservice.model.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {
private final ProductRepository productRepository;
private final KafkaTemplate kafkaTemplate;


    @Override
    public Boolean productsAvailable(List<OrderLineItem> lineItems) {
        for (OrderLineItem lineItem : lineItems){
            Optional<Product> product = productRepository.findById(lineItem.getProductId());
            if (product.isEmpty())
                return false;
         if (lineItem.getQuantity() > product.get().getQuantity() ) {
                return false; // Not enough quantity available
            }
        }

        return true;
    }

    @Override
    @Transactional("transactionManager")
    public void reserveProducts(List<OrderLineItem> lineItems,String orderId) {
        log.info("**** Checking if Products are in Stock **************");
        Boolean productsReserved = true;
        for (OrderLineItem lineItem : lineItems) {
            Optional<Product> product = productRepository.findById(lineItem.getProductId());

            if (product.isEmpty()){
                productsReserved = false;
                break;
            }
            Product productItem = product.get();

            if (lineItem.getQuantity() > productItem.getQuantity() ) {
                productsReserved = false;
                break;
            }

            Integer updatedQuantity = productItem.getQuantity() - lineItem.getQuantity();
            lineItem.setPrice(productItem.getPrice());
            product.get().setQuantity(updatedQuantity);
            productRepository.save(product.get());

    }
      if (productsReserved) {

          log.info("**** Products Reserved Successfully **************8");

          ProductReservedEvent responseEvent = new ProductReservedEvent();
          responseEvent.setOrderLineItems(lineItems);
          ProducerRecord producerRecord = new ProducerRecord<>(
                  Topics.PRODUCT_RESERVE_RESPONSE_TOPIC, orderId, responseEvent
          );

          kafkaTemplate.send(producerRecord);
          log.info("**** Products pushed in  **************" + Topics.PRODUCT_RESERVE_RESPONSE_TOPIC);

      }else{
            log.info("**** Products Reservation Failed!!  **************");
          ProductReserveFailedEvent  responseEvent = new ProductReserveFailedEvent();
          responseEvent.setOrderLineItems(lineItems);
          ProducerRecord producerRecord = new ProducerRecord<>(
                  Topics.PRODUCT_RESERVE_RESPONSE_TOPIC,orderId,responseEvent
          );

          kafkaTemplate.send(producerRecord);
            log.info("**** Products pushed in  **************" + Topics.PRODUCT_RESERVE_RESPONSE_TOPIC);
        }


    }
}
