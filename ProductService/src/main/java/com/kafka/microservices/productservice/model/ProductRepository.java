package com.kafka.microservices.productservice.model;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findBySku(String sku);


    @Modifying
    @Transactional
    @Query(value = "TRUNCATE TABLE product CASCADE", nativeQuery = true)
    void deleteProducts();
}
