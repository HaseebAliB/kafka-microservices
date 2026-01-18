package com.kafka.microservices.productservice;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kafka.microservices.productservice.model.Product;
import com.kafka.microservices.productservice.model.ProductRepository;
import com.kafka.microservices.productservice.services.CachingService;
import com.kafka.microservices.productservice.services.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Component
@Slf4j
public class ProductLoader implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final ProductService productService;

    private final ObjectMapper objectMapper;
    private final CachingService cachingService;

    @Value("${product.datafile}")
    private String productDataFile;

    public ProductLoader(ProductRepository productRepository, ObjectMapper objectMapper,
                         CachingService cachingService, ProductService productService) {
        this.productRepository = productRepository;
        this.objectMapper = objectMapper;
        this.cachingService = cachingService;
        this.productService = productService;
    }

    @Override
    public void run(String... args) throws Exception {
       // productRepository.deleteProducts();
        log.info("Loading products from Products Json...");

        if (productRepository.count() > 0) {
            System.out.println("Products already exist. Skipping bootstrap.");
            productRepository.findAll().forEach(cachingService::saveProduct);
            return;
        }


        Path path = Paths.get(productDataFile);
        if (Files.exists(path)) {
            List<Product> products = objectMapper.readValue(
                    path.toFile(),
                    new TypeReference<List<Product>>() {
                    }
            );

            log.debug("Loaded " + products.size() + " sample products.");
            productService.bootstrapProducts(products);
        }else{
            log.error("****Could not find file products.json.......");
        }

        //productRepository.saveAll(products);



    }
}
