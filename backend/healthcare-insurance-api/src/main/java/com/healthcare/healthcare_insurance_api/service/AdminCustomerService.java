package com.healthcare.healthcare_insurance_api.service;

import com.healthcare.healthcare_insurance_api.dto.CustomerResponse;
import com.healthcare.healthcare_insurance_api.entity.User;
import com.healthcare.healthcare_insurance_api.enums.Role;
import com.healthcare.healthcare_insurance_api.enums.UserStatus;
import com.healthcare.healthcare_insurance_api.repository.UserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class AdminCustomerService {

    private final UserRepository userRepository;


    public AdminCustomerService(
            UserRepository userRepository) {

        this.userRepository = userRepository;
    }


    // =========================================================
    // GET ALL CUSTOMERS
    // =========================================================

    public List<CustomerResponse> getAllCustomers() {

        List<User> users =
                userRepository.findByRole(Role.USER);

        return users.stream()
                .map(this::convertToResponse)
                .toList();
    }


    // =========================================================
    // GET CUSTOMER BY ID
    // =========================================================

    public CustomerResponse getCustomerById(
            Long id) {

        User user =
                userRepository.findById(id)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Customer not found"
                                )
                        );


        // Only USER accounts are customers

        if (user.getRole() != Role.USER) {

            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Customer not found"
            );
        }


        return convertToResponse(user);
    }


    // =========================================================
    // UPDATE CUSTOMER STATUS
    // =========================================================

    public CustomerResponse updateCustomerStatus(
            Long id,
            UserStatus newStatus) {

        User user =
                userRepository.findById(id)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Customer not found"
                                )
                        );


        // Admin cannot change another admin's status
        // Only USER records are customers

        if (user.getRole() != Role.USER) {

            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Customer not found"
            );
        }


        // Update status

        user.setStatus(newStatus);


        // Save to database

        User updatedUser =
                userRepository.save(user);


        return convertToResponse(updatedUser);
    }


    // =========================================================
    // ENTITY -> DTO
    // =========================================================

    private CustomerResponse convertToResponse(
            User user) {

        return new CustomerResponse(

                user.getId(),

                user.getFullName(),
                

                user.getEmail(),

                user.getPhone(),

                user.getDateOfBirth(),

                user.getGender(),

                user.getAddress(),

                user.getStatus() != null
                        ? user.getStatus().name()
                        : null,

                user.getCreatedAt()
        );
    }
}