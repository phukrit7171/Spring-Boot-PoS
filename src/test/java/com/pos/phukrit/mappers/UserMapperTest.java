package com.pos.phukrit.mappers;

import com.pos.phukrit.dtos.UserDto;
import com.pos.phukrit.models.UserModel;
import com.pos.phukrit.models.UserRole;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {

    private final UserMapper mapper = Mappers.getMapper(UserMapper.class);

    @Test
    void toDto_ignoresPassword() {
        UserModel user = new UserModel();
        user.setId(1L);
        user.setName("Alice");
        user.setUsername("alice");
        user.setPassword("secret");
        user.setRole(UserRole.ADMIN);

        UserDto dto = mapper.toDto(user);
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getUsername()).isEqualTo("alice");
        assertThat(dto.getPassword()).isNull();
    }

    @Test
    void toEntity_ignoresPasswordFromDto() {
        UserDto dto = new UserDto();
        dto.setId(2L);
        dto.setName("Bob");
        dto.setUsername("bob");
        dto.setPassword("should-not-map");

        UserModel entity = mapper.toEntity(dto);
        assertThat(entity.getId()).isEqualTo(2L);
        assertThat(entity.getUsername()).isEqualTo("bob");
        assertThat(entity.getPassword()).isNull();
    }
}
