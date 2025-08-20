package com.pos.phukrit.services;

import com.pos.phukrit.dtos.CustomerReqDto;
import com.pos.phukrit.dtos.CustomerResDto;
import com.pos.phukrit.mappers.CustomerMapper;
import com.pos.phukrit.models.CustomerModel;
import com.pos.phukrit.repositories.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper = CustomerMapper.INSTANCE;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public CustomerResDto createCustomer(CustomerReqDto customerReqDto) {
        CustomerModel customerModel = customerMapper.toCustomerModel(customerReqDto);
        CustomerModel savedCustomer = customerRepository.save(customerModel);
        return customerMapper.toCustomerResDto(savedCustomer);
    }

    public Optional<CustomerResDto> getCustomerByPhoneNumber(String phoneNumber) {
        return customerRepository.findByPhoneNumber(phoneNumber)
                .map(customerMapper::toCustomerResDto);
    }
}