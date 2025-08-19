package com.pos.phukrit.mappers;

import com.pos.phukrit.dtos.CategoryDto;
import com.pos.phukrit.models.Category;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class CategoryMapperTest {

    private final CategoryMapper mapper = Mappers.getMapper(CategoryMapper.class);

    @Test
    void roundTrip_mapping() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Beverages");
        category.setDescription("Drinks");

        CategoryDto dto = mapper.toDto(category);
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Beverages");
        assertThat(dto.getDescription()).isEqualTo("Drinks");

        Category back = mapper.toEntity(dto);
        assertThat(back.getId()).isEqualTo(1L);
        assertThat(back.getName()).isEqualTo("Beverages");
        assertThat(back.getDescription()).isEqualTo("Drinks");
    }
}
