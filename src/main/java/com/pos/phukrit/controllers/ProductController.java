package com.pos.phukrit.controllers;

import com.pos.phukrit.dtos.ProductReqDto;
import com.pos.phukrit.dtos.ProductResDto;
import com.pos.phukrit.services.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
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
    public ProductResDto createProduct(@Valid @RequestBody ProductReqDto productReqDto) {
        return productService.createProduct(productReqDto);
    }

    // --- NEW ENDPOINT: UPDATE ---
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ProductResDto> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductReqDto productReqDto) {
        return productService.updateProduct(id, productReqDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // --- NEW ENDPOINT: DELETE ---
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        if (productService.deleteProduct(id)) {
            return ResponseEntity.noContent().build(); // Standard 204 No Content response
        }
        return ResponseEntity.notFound().build();
    }
}