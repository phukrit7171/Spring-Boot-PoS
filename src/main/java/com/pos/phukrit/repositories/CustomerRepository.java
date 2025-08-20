package com.pos.phukrit.repositories;

import com.pos.phukrit.models.CustomerModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerModel, Long> {

    // This query is essential for our business logic
    Optional<CustomerModel> findByPhoneNumber(String phoneNumber);

}