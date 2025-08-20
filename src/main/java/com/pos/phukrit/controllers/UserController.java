package com.pos.phukrit.controllers;

import com.pos.phukrit.dtos.UserReqDto;
import com.pos.phukrit.dtos.UserResDto;
import com.pos.phukrit.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // GET /api/users - Get all users
    @GetMapping
    public List<UserResDto> getAllUsers() {
        return userService.getAllUsers();
    }

    // GET /api/users/{id} - Get user by ID
    @GetMapping("/{id}")
    public ResponseEntity<UserResDto> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/users - Create a new user
    @PostMapping
    public UserResDto createUser(@RequestBody UserReqDto userReqDto) {
        return userService.createUser(userReqDto);
    }
}