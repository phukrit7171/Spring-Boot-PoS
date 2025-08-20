package com.pos.phukrit.services;

import com.pos.phukrit.dtos.ProductReqDto;
import com.pos.phukrit.dtos.ProductResDto;
import com.pos.phukrit.mappers.ProductMapper;
import com.pos.phukrit.models.ProductModel;
import com.pos.phukrit.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository; // Field is now final
    private final ProductMapper productMapper = ProductMapper.INSTANCE;

    // Dependency is injected via the constructor
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // Get all products
    public List<ProductResDto> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(productMapper::toProductResDto)
                .collect(Collectors.toList());
    }

    // Get a single product by ID
    public Optional<ProductResDto> getProductById(Long id) {
        return productRepository.findById(id)
                .map(productMapper::toProductResDto);
    }

    // Create a new product
    public ProductResDto createProduct(ProductReqDto productReqDto) {
        ProductModel productModel = productMapper.toProductModel(productReqDto);
        ProductModel savedProduct = productRepository.save(productModel);
        return productMapper.toProductResDto(savedProduct);
    }

    // Search for products by name
    public List<ProductResDto> searchProductsByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(productMapper::toProductResDto)
                .collect(Collectors.toList());
    }

    // --- ENHANCEMENT ---
    // This is the new, centralized method for handling stock reduction.
    public void reduceStock(Long productId, int quantity) {
        // 1. Find the product
        ProductModel product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found: " + productId));

        // 2. Check the stock
        if (product.getStock() < quantity) {
            throw new RuntimeException("Not enough stock for: " + product.getName());
        }

        // 3. Reduce the stock
        product.setStock(product.getStock() - quantity);
        // Note: We don't need to call productRepository.save() here.
        // Because the method is @Transactional, JPA will automatically detect
        // the change to the 'product' object and save it to the database
        // when the transaction commits.
    }
}