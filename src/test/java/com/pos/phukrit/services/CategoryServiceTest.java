package com.pos.phukrit.services;

import com.pos.phukrit.models.Category;
import com.pos.phukrit.repositories.CategoryRepository;
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

class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createCategory_saves() {
        Category c = new Category();
        c.setName("Beverages");
        when(categoryRepository.save(any(Category.class))).thenAnswer(inv -> inv.getArgument(0));
        Category saved = categoryService.createCategory(c);
        assertThat(saved.getName()).isEqualTo("Beverages");
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void updateCategory_updatesFields_whenFound() {
        Category existing = new Category();
        existing.setId(1L);
        existing.setName("Old");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(categoryRepository.save(any(Category.class))).thenAnswer(inv -> inv.getArgument(0));

        Category details = new Category();
        details.setName("New");
        details.setDescription("Desc");

        Optional<Category> updated = categoryService.updateCategory(1L, details);
        assertThat(updated).isPresent();
        assertThat(updated.get().getName()).isEqualTo("New");
        assertThat(updated.get().getDescription()).isEqualTo("Desc");
    }

    @Test
    void deleteCategory_returnsTrueIfFound_elseFalse() {
        Category c = new Category();
        c.setId(2L);
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(c));
        when(categoryRepository.findById(3L)).thenReturn(Optional.empty());

        assertThat(categoryService.deleteCategory(2L)).isTrue();
        assertThat(categoryService.deleteCategory(3L)).isFalse();

        verify(categoryRepository).delete(c);
    }

    @Test
    void queries_delegateToRepository() {
        when(categoryRepository.findAll()).thenReturn(List.of(new Category(), new Category()));
        when(categoryRepository.findById(9L)).thenReturn(Optional.of(new Category()));

        assertThat(categoryService.getAllCategories()).hasSize(2);
        assertThat(categoryService.getCategoryById(9L)).isPresent();
    }
}
