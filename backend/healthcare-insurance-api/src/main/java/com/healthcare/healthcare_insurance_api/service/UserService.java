package com.healthcare.healthcare_insurance_api.service;

import java.time.LocalDate;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.healthcare.healthcare_insurance_api.dto.RegisterRequest;
import com.healthcare.healthcare_insurance_api.dto.UserResponse;
import com.healthcare.healthcare_insurance_api.entity.User;
import com.healthcare.healthcare_insurance_api.enums.Role;
import com.healthcare.healthcare_insurance_api.enums.UserStatus;
import com.healthcare.healthcare_insurance_api.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;


    // Constructor

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    // Register User

    @Transactional
    public UserResponse register(RegisterRequest request) {

        // Check whether email already exists

        if (userRepository.existsByEmail(request.getEmail())) {

            throw new RuntimeException(
                "Email is already registered"
            );
        }


        // Create User entity

        User user = new User();


        user.setFullName(
            request.getFullName()
        );


        user.setEmail(
            request.getEmail()
        );


        user.setPhone(
            request.getPhone()
        );


        // Encrypt password before saving

        user.setPasswordHash(
            passwordEncoder.encode(
                request.getPassword()
            )
        );


        // Set default role

        user.setRole(
            Role.USER
        );


        // Set default status

        user.setStatus(
            UserStatus.ACTIVE
        );


        // Date of birth

        user.setDateOfBirth(
            request.getDateOfBirth()
        );


        user.setGender(
            request.getGender()
        );


        user.setAddress(
            request.getAddress()
        );


        // Save user

        User savedUser =
            userRepository.save(user);


        // Convert Entity to Response DTO

        return convertToResponse(savedUser);
    }


    // Check Email

    public boolean emailExists(String email) {

        return userRepository.existsByEmail(email);
    }


    // Convert User Entity to UserResponse

    private UserResponse convertToResponse(User user) {

        String dateOfBirth = null;

        if (user.getDateOfBirth() != null) {

            dateOfBirth =
                user.getDateOfBirth().toString();
        }


        String role = null;

        if (user.getRole() != null) {

            role =
                user.getRole().name();
        }


        String status = null;

        if (user.getStatus() != null) {

            status =
                user.getStatus().name();
        }


        return new UserResponse(

            user.getId(),

            user.getFullName(),

            user.getEmail(),

            user.getPhone(),

            role,

            dateOfBirth,

            user.getGender(),

            user.getAddress(),

            status
        );
    }
}