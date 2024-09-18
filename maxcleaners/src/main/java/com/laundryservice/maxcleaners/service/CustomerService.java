package com.laundryservice.maxcleaners.service;

import com.laundryservice.maxcleaners.dto.LoginRequest;
import com.laundryservice.maxcleaners.dto.SignupRequest;
import com.laundryservice.maxcleaners.exception.CustomException;
import com.laundryservice.maxcleaners.model.Customer;
import com.laundryservice.maxcleaners.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;


/**
 * Author Tejesh
 */
@Service
public class CustomerService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public CustomerService(CustomerRepository customerRepository, @Lazy PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Customer signup(SignupRequest request) {
        logger.info("Attempting to sign up customer with phone number: {}", request.getPhoneNumber());

        // Validate request fields
        if (Objects.isNull(request.getFirstName()) || request.getFirstName().trim().isEmpty()) {
            logger.error("First name is required");
            throw new CustomException("First name is required", HttpStatus.BAD_REQUEST.value());
        }
        if (Objects.isNull(request.getLastName()) || request.getLastName().trim().isEmpty()) {
            logger.error("Last name is required");
            throw new CustomException("Last name is required", HttpStatus.BAD_REQUEST.value());
        }
        if (Objects.isNull(request.getPhoneNumber()) || request.getPhoneNumber().trim().isEmpty()) {
            logger.error("Phone number is required");
            throw new CustomException("Phone number is required", HttpStatus.BAD_REQUEST.value());
        }
        if (Objects.isNull(request.getPassword()) || request.getPassword().trim().isEmpty()) {
            logger.error("Password is required");
            throw new CustomException("Password is required", HttpStatus.BAD_REQUEST.value());
        }
        if (Objects.isNull(request.getAddress()) || request.getAddress().trim().isEmpty()) {
            logger.error("Address is required");
            throw new CustomException("Address is required", HttpStatus.BAD_REQUEST.value());
        }

        // Check if phone number is already registered
        if (customerRepository.findByPhoneNumber(request.getPhoneNumber()).isPresent()) {
            logger.error("Phone number {} is already registered", request.getPhoneNumber());
            throw new CustomException("Phone number already registered", HttpStatus.BAD_REQUEST.value());
        }

        // Get the current user's authentication information
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String createdBy = (authentication != null && !"anonymousUser".equals(authentication.getName()))
                ? authentication.getName()
                : "system";

        // Create and save the new customer
        Customer customer = new Customer();
        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setPhoneNumber(request.getPhoneNumber());
        customer.setPassword(passwordEncoder.encode(request.getPassword()));
        customer.setAddress(request.getAddress());
        customer.setCreatedBy(createdBy);
        customer.setUpdatedBy(createdBy);
        customer.setCreatedAt(LocalDateTime.now());
        customer.setUpdatedAt(LocalDateTime.now());

        try {
            Customer savedCustomer = customerRepository.save(customer);
            logger.info("Customer with phone number {} registered successfully", request.getPhoneNumber());
            return savedCustomer;
        } catch (Exception e) {
            logger.error("Failed to register customer with phone number {} due to: {}", request.getPhoneNumber(), e.getMessage());
            throw new CustomException("Failed to register customer. Please try again later.", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }


    public Customer login(LoginRequest request) {
        logger.info("Attempting to log in customer with phone number: {}", request.getPhoneNumber());

        // Validate input fields
        if (Objects.isNull(request.getPhoneNumber()) || request.getPhoneNumber().trim().isEmpty()) {
            logger.error("Phone number is required for login");
            throw new CustomException("Phone number is required", HttpStatus.BAD_REQUEST.value());
        }

        if (Objects.isNull(request.getPassword()) || request.getPassword().trim().isEmpty()) {
            logger.error("Password is required for login");
            throw new CustomException("Password is required", HttpStatus.BAD_REQUEST.value());
        }

        // Check if the customer exists
        Customer customer = customerRepository.findByPhoneNumber(request.getPhoneNumber())
                .orElseThrow(() -> {
                    logger.error("User not found with phone number: {}", request.getPhoneNumber());
                    return new CustomException("User not found", HttpStatus.NOT_FOUND.value());
                });

        // Validate password
        if (!passwordEncoder.matches(request.getPassword(), customer.getPassword())) {
            logger.error("Invalid password for phone number: {}", request.getPhoneNumber());
            throw new CustomException("Invalid password", HttpStatus.UNAUTHORIZED.value());
        }
        logger.info("Customer with phone number {} logged in successfully", request.getPhoneNumber());
        return customer;
    }

    public Customer findByPhoneNumber(String phoneNumber) {
        return customerRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND.value()));
    }
}
