package com.pos.phukrit.services;

import com.pos.phukrit.models.Category;
import com.pos.phukrit.models.Product;
import com.pos.phukrit.repositories.CategoryRepository;
import com.pos.phukrit.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public Product createProduct(Product product) {
        // Ensure category exists if provided
        if (product.getCategory() != null && product.getCategory().getId() != null) {
            Optional<Category> category = categoryRepository.findById(product.getCategory().getId());
            category.ifPresent(product::setCategory);
        }
        return productRepository.save(product);
    }

    public Optional<Product> updateProduct(Long id, Product productDetails) {
        return productRepository.findById(id).map(product -> {
            product.setName(productDetails.getName());
            product.setDescription(productDetails.getDescription());
            product.setPrice(productDetails.getPrice());
            product.setStockQuantity(productDetails.getStockQuantity());
            
            // Update category if provided
            if (productDetails.getCategory() != null && productDetails.getCategory().getId() != null) {
                Optional<Category> category = categoryRepository.findById(productDetails.getCategory().getId());
                category.ifPresent(product::setCategory);
            }
            
            return productRepository.save(product);
        });
    }

    public boolean deleteProduct(Long id) {
        return productRepository.findById(id).map(product -> {
            productRepository.delete(product);
            return true;
        }).orElse(false);
    }

    public List<Product> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    public List<Product> searchProductsByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }
}