package com.pos.phukrit.controllers;

import com.pos.phukrit.dtos.UserReqDto;
import com.pos.phukrit.dtos.UserResDto;
import com.pos.phukrit.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserResDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResDto> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public UserResDto createUser(@Valid @RequestBody UserReqDto userReqDto) {
        return userService.createUser(userReqDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResDto> updateUser(@PathVariable Long id, @Valid @RequestBody UserReqDto userReqDto) {
        UserResDto updatedUser = userService.updateUser(id, userReqDto);
        if (updatedUser != null) {
            return ResponseEntity.ok(updatedUser);
        }
        return ResponseEntity.notFound().build();
    }

    // Debug endpoint to check if username exists
    @GetMapping("/check-username/{username}")
    public ResponseEntity<Map<String, Object>> checkUsername(@PathVariable String username) {
        Map<String, Object> response = new HashMap<>();
        response.put("requested_username", username);
        response.put("username_length", username.length());
        response.put("username_bytes", java.util.Arrays.toString(username.getBytes()));
        
        List<UserResDto> allUsers = userService.getAllUsers();
        List<Map<String, Object>> existingUsers = new ArrayList<>();
        
        for (UserResDto user : allUsers) {
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", user.getId());
            userInfo.put("username", user.getUsername());
            userInfo.put("username_length", user.getUsername().length());
            userInfo.put("username_bytes", java.util.Arrays.toString(user.getUsername().getBytes()));
            userInfo.put("exact_match", username.equals(user.getUsername()));
            userInfo.put("case_insensitive_match", username.equalsIgnoreCase(user.getUsername()));
            existingUsers.add(userInfo);
        }
        
        response.put("existing_users", existingUsers);
        response.put("username_exists", allUsers.stream().anyMatch(u -> u.getUsername().equals(username)));
        
        return ResponseEntity.ok(response);
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (userService.deleteUser(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
    
}
