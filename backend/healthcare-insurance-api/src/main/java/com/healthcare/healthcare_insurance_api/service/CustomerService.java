package com.healthcare.healthcare_insurance_api.service;

import org.springframework.stereotype.Service;

import com.healthcare.healthcare_insurance_api.dto.ProfileResponse;
import com.healthcare.healthcare_insurance_api.dto.ProfileUpdateRequest;
import com.healthcare.healthcare_insurance_api.entity.User;
import com.healthcare.healthcare_insurance_api.repository.UserRepository;

@Service
public class CustomerService {

    private final UserRepository userRepository;

    public CustomerService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // ==============================
    // GET PROFILE
    // ==============================

    public ProfileResponse getProfile(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        return convertToProfileResponse(user);
    }


    // ==============================
    // UPDATE PROFILE
    // ==============================

    public ProfileResponse updateProfile(
            String email,
            ProfileUpdateRequest request) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        user.setFullName(request.getFullName());

        user.setPhone(request.getPhone());

        user.setDateOfBirth(
                request.getDateOfBirth()
        );

        user.setGender(
                request.getGender()
        );

        user.setAddress(
                request.getAddress()
        );

        User updatedUser =
                userRepository.save(user);

        return convertToProfileResponse(updatedUser);
    }


    // ==============================
    // CONVERT ENTITY → DTO
    // ==============================

    private ProfileResponse convertToProfileResponse(
            User user) {

        return new ProfileResponse(

                user.getId(),

                user.getFullName(),

                user.getEmail(),

                user.getPhone(),

                user.getDateOfBirth(),

                user.getGender(),

                user.getAddress(),

                user.getRole(),

                user.getStatus()
        );
    }
}