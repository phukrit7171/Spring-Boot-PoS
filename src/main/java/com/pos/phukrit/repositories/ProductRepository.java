package com.pos.phukrit.repositories;

import com.pos.phukrit.models.ProductModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<ProductModel, Long> {

    Optional<ProductModel> findByName(String name);

    List<ProductModel> findByNameContainingIgnoreCase(String name);
}