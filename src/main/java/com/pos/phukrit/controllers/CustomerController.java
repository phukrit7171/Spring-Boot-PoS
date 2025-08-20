package com.pos.phukrit.controllers;

import com.pos.phukrit.dtos.CustomerReqDto;
import com.pos.phukrit.dtos.CustomerResDto;
import com.pos.phukrit.services.CustomerService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customers")
@PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    public CustomerResDto createCustomer(@RequestBody CustomerReqDto customerReqDto) {
        return customerService.createCustomer(customerReqDto);
    }

    @GetMapping("/lookup/by-phone/{phoneNumber}")
    public ResponseEntity<CustomerResDto> getCustomerByPhoneNumber(@PathVariable String phoneNumber) {
        return customerService.getCustomerByPhoneNumber(phoneNumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}