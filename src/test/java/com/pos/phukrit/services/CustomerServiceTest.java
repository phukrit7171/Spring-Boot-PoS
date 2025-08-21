package com.pos.phukrit.services;

import com.pos.phukrit.dtos.CustomerReqDto;
import com.pos.phukrit.dtos.CustomerResDto;
import com.pos.phukrit.models.CustomerModel;
import com.pos.phukrit.repositories.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    private CustomerModel testCustomer;
    private CustomerReqDto customerReqDto;

    @BeforeEach
    void setUp() {
        testCustomer = new CustomerModel();
        testCustomer.setId(1L);
        testCustomer.setName("John Doe");
        testCustomer.setPhoneNumber("1234567890");
        testCustomer.setPoints(50);

        customerReqDto = new CustomerReqDto();
        customerReqDto.setName("John Doe");
        customerReqDto.setPhoneNumber("1234567890");
    }

    @Test
    void createCustomer_shouldReturnSavedCustomer() {
        // Arrange
        // When the save method is called with any CustomerModel, return our testCustomer
        when(customerRepository.save(any(CustomerModel.class))).thenReturn(testCustomer);

        // Act
        CustomerResDto result = customerService.createCustomer(customerReqDto);

        // Assert
        assertNotNull(result);
        assertEquals("John Doe", result.getName());
        assertEquals("1234567890", result.getPhoneNumber());
        verify(customerRepository, times(1)).save(any(CustomerModel.class));
    }

    @Test
    void getCustomerByPhoneNumber_whenCustomerExists_shouldReturnCustomer() {
        // Arrange
        when(customerRepository.findByPhoneNumber("1234567890")).thenReturn(Optional.of(testCustomer));

        // Act
        Optional<CustomerResDto> result = customerService.getCustomerByPhoneNumber("1234567890");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("John Doe", result.get().getName());
        verify(customerRepository, times(1)).findByPhoneNumber("1234567890");
    }
}