package com.pos.phukrit.dtos;

import com.pos.phukrit.models.UserRole;

public record UserResponse(
    Long id,
    String username,
    String email,
    UserRole role
) {}