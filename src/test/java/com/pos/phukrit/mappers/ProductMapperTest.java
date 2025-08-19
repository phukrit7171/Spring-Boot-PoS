package com.pos.phukrit.mappers;

import com.pos.phukrit.dtos.ProductDto;
import com.pos.phukrit.models.Category;
import com.pos.phukrit.models.Product;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class ProductMapperTest {

    private final ProductMapper mapper = Mappers.getMapper(ProductMapper.class);

    @Test
    void toDto_mapsCategoryFields() {
        Category category = new Category();
        category.setId(3L);
        category.setName("Snacks");

        Product p = new Product();
        p.setId(1L);
        p.setName("Chips");
        p.setDescription("Tasty");
        p.setPrice(new BigDecimal("2.50"));
        p.setStockQuantity(100);
        p.setCategory(category);

        ProductDto dto = mapper.toDto(p);
        assertThat(dto.getCategoryId()).isEqualTo(3L);
        assertThat(dto.getCategoryName()).isEqualTo("Snacks");
    }

    @Test
    void toEntity_setsNestedCategoryId() {
        ProductDto dto = new ProductDto();
        dto.setId(10L);
        dto.setName("Soda");
        dto.setCategoryId(5L);

        Product entity = mapper.toEntity(dto);
        assertThat(entity.getCategory()).isNotNull();
        assertThat(entity.getCategory().getId()).isEqualTo(5L);
    }
}
