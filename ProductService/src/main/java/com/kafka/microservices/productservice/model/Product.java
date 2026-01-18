package com.kafka.microservices.productservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sku;
    private String name;

    @Column(length = 2000)
    private String description;

    private String brand;
    private String category;
    private Double price;
    private String currency;
    private Integer stockQuantity;
    private String thumbnailUrl;

    @ElementCollection
    @CollectionTable(name = "product_images", joinColumns = @JoinColumn(name = "product_id"))
    private List<String> imageUrls;

    private Double rating;
    private Integer reviewsCount;

    @ElementCollection
    @CollectionTable(name = "product_tags", joinColumns = @JoinColumn(name = "product_id"))
    private List<String> tags;

    private boolean isActive;

    // Getters and setters...
}

