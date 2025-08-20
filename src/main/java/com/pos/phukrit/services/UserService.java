package com.pos.phukrit.services;

import com.pos.phukrit.dtos.UserReqDto;
import com.pos.phukrit.dtos.UserResDto;
import com.pos.phukrit.mappers.UserMapper;
import com.pos.phukrit.models.UserModel;
import com.pos.phukrit.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private final UserMapper userMapper = UserMapper.INSTANCE;

    // Get all users
    public List<UserResDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toUserResDto)
                .collect(Collectors.toList());
    }

    // Get a single user by ID
    public Optional<UserResDto> getUserById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toUserResDto);
    }

    // Create a new user
    public UserResDto createUser(UserReqDto userReqDto) {
        // Here you would add logic to check if username or email already exists
        // and also to hash the password before saving.
        // For now, we will keep it simple.
        UserModel userModel = userMapper.toUserModel(userReqDto);
        UserModel savedUser = userRepository.save(userModel);
        return userMapper.toUserResDto(savedUser);
    }

    // We can add methods for updating and deleting users later.
}