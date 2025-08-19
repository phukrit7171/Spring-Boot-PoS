package com.pos.phukrit.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pos.phukrit.models.UserModel;

@Repository
public interface UserRepository extends JpaRepository<UserModel, Long> {
    
    // This query now returns an Optional of UserModel
    Optional<UserModel> findByUsername(String username);
    Optional<UserModel> findByEmail(String email);
}