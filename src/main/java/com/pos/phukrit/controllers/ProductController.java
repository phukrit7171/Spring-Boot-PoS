package com.pos.phukrit.controllers;

import com.pos.phukrit.dtos.ProductReqDto;
import com.pos.phukrit.dtos.ProductResDto;
import com.pos.phukrit.services.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // GET /api/products - Get all products
    @GetMapping
    public List<ProductResDto> getAllProducts() {
        return productService.getAllProducts();
    }

    // GET /api/products/search?name={name} - Search products by name
    @GetMapping("/search")
    public List<ProductResDto> searchProductsByName(@RequestParam String name) {
        return productService.searchProductsByName(name);
    }

    // GET /api/products/{id} - Get product by ID
    @GetMapping("/{id}")
    public ResponseEntity<ProductResDto> getProductById(@PathVariable Long id) {
        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/products - Create a new product
    @PostMapping
    public ProductResDto createProduct(@RequestBody ProductReqDto productReqDto) {
        return productService.createProduct(productReqDto);
    }
}