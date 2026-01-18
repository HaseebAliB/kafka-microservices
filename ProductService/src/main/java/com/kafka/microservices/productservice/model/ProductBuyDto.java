package com.kafka.microservices.productservice.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductBuyDto {
Long productId;
Integer quantity;

}
