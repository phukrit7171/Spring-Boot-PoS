package com.pos.phukrit.services;

import com.pos.phukrit.dtos.UserReqDto;
import com.pos.phukrit.dtos.UserResDto;
import com.pos.phukrit.mappers.UserMapper;
import com.pos.phukrit.models.UserModel;
import com.pos.phukrit.repositories.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper = UserMapper.INSTANCE;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // --- METHOD FOR SECURITY ---
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserModel userModel = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return new User(
                userModel.getUsername(),
                userModel.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + userModel.getRole().name()))
        );
    }

    // --- METHOD CALLED BY CONTROLLER ---
    public List<UserResDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toUserResDto)
                .collect(Collectors.toList());
    }

    public Optional<UserResDto> getUserById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toUserResDto);
    }

    public UserResDto createUser(UserReqDto userReqDto) {
        UserModel userModel = userMapper.toUserModel(userReqDto);
        userModel.setPassword(passwordEncoder.encode(userReqDto.getPassword()));
        UserModel savedUser = userRepository.save(userModel);
        return userMapper.toUserResDto(savedUser);
    }
}