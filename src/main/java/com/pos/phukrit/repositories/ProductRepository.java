package com.pos.phukrit.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.pos.phukrit.models.ProductModel;

@Repository
public interface ProductRepository extends JpaRepository<ProductModel, Long> {
    
    // This query now returns an Optional of ProductModel
    Optional<ProductModel> findByName(String name);
    Optional<ProductModel> findByDescription(String description);

}