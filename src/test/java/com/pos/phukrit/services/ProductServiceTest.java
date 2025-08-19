package com.pos.phukrit.services;

import com.pos.phukrit.models.Category;
import com.pos.phukrit.models.Product;
import com.pos.phukrit.repositories.CategoryRepository;
import com.pos.phukrit.repositories.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createProduct_resolvesCategoryIfProvided() {
        Product p = new Product();
        Category c = new Category();
        c.setId(10L);
        p.setCategory(c);

        Category found = new Category();
        found.setId(10L);
        found.setName("Beverages");

        when(categoryRepository.findById(10L)).thenReturn(Optional.of(found));
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        Product saved = productService.createProduct(p);

        assertThat(saved.getCategory()).isNotNull();
        assertThat(saved.getCategory().getName()).isEqualTo("Beverages");
    }

    @Test
    void updateProduct_updatesFields_andCategoryIfProvided() {
        Product existing = new Product();
        existing.setId(1L);
        existing.setName("Old");

        when(productRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        Product details = new Product();
        details.setName("New");
        details.setDescription("Desc");
        details.setPrice(java.math.BigDecimal.TEN);
        details.setStockQuantity(5);
        Category newCat = new Category();
        newCat.setId(22L);
        details.setCategory(newCat);

        Category foundCat = new Category();
        foundCat.setId(22L);
        foundCat.setName("Snacks");
        when(categoryRepository.findById(22L)).thenReturn(Optional.of(foundCat));

        Optional<Product> updated = productService.updateProduct(1L, details);
        assertThat(updated).isPresent();
        assertThat(updated.get().getName()).isEqualTo("New");
        assertThat(updated.get().getCategory().getName()).isEqualTo("Snacks");
    }

    @Test
    void deleteProduct_returnsTrueIfFound_elseFalse() {
        Product p = new Product();
        p.setId(2L);
        when(productRepository.findById(2L)).thenReturn(Optional.of(p));
        when(productRepository.findById(3L)).thenReturn(Optional.empty());

        assertThat(productService.deleteProduct(2L)).isTrue();
        assertThat(productService.deleteProduct(3L)).isFalse();

        verify(productRepository).delete(p);
    }

    @Test
    void queries_delegateToRepository() {
        when(productRepository.findAll()).thenReturn(List.of(new Product(), new Product(), new Product()));
        when(productRepository.findById(9L)).thenReturn(Optional.of(new Product()));
        when(productRepository.findByCategoryId(1L)).thenReturn(List.of(new Product()));
        when(productRepository.findByNameContainingIgnoreCase("tea")).thenReturn(List.of(new Product(), new Product()));

        assertThat(productService.getAllProducts()).hasSize(3);
        assertThat(productService.getProductById(9L)).isPresent();
        assertThat(productService.getProductsByCategory(1L)).hasSize(1);
        assertThat(productService.searchProductsByName("tea")).hasSize(2);
    }
}
