package com.healthcare.healthcare_insurance_api.service;

import com.healthcare.healthcare_insurance_api.dto.ProfileResponse;
import com.healthcare.healthcare_insurance_api.dto.UpdateProfileRequest;
import com.healthcare.healthcare_insurance_api.entity.User;
import com.healthcare.healthcare_insurance_api.repository.UserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@Service
public class ProfileService {

    private final UserRepository userRepository;

    public ProfileService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public ProfileResponse getProfile(String email) {

        User user = getUserByEmail(email);

        return convertToResponse(user);
    }

    public ProfileResponse updateProfile(
            String email,
            UpdateProfileRequest request) {

        if (request == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Profile data is required"
            );
        }

        validateProfile(request);

        User user = getUserByEmail(email);

        user.setFullName(
                request.getFullName().trim()
        );

        user.setPhone(
                request.getPhone().trim()
        );

        user.setDateOfBirth(
                request.getDateOfBirth()
        );

        user.setGender(
                request.getGender() != null
                        ? request.getGender().trim()
                        : null
        );

        user.setAddress(
                request.getAddress() != null
                        ? request.getAddress().trim()
                        : null
        );

        User updatedUser =
                userRepository.save(user);

        return convertToResponse(updatedUser);
    }

    private User getUserByEmail(String email) {

        if (email == null || email.isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Authenticated user not found"
            );
        }

        return userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "User not found"
                        )
                );
    }

    private void validateProfile(
            UpdateProfileRequest request) {

        // Full Name
        if (request.getFullName() == null ||
                request.getFullName().trim().isEmpty()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Full name is required"
            );
        }

        if (request.getFullName().trim().length() < 2) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Full name must contain at least 2 characters"
            );
        }

        if (request.getFullName().trim().length() > 100) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Full name is too long"
            );
        }

        // Phone
        if (request.getPhone() == null ||
                request.getPhone().trim().isEmpty()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Phone number is required"
            );
        }

        String phone =
                request.getPhone().trim();

        if (!phone.matches("[6-9][0-9]{9}")) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Phone number must be a valid 10 digit Indian mobile number"
            );
        }

        // Date of Birth
        if (request.getDateOfBirth() != null &&
                request.getDateOfBirth().isAfter(LocalDate.now())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Date of birth cannot be in the future"
            );
        }

        // Gender
        if (request.getGender() != null &&
                !request.getGender().trim().isEmpty()) {

            String gender =
                    request.getGender().trim();

            boolean validGender =
                    gender.equalsIgnoreCase("Male") ||
                    gender.equalsIgnoreCase("Female") ||
                    gender.equalsIgnoreCase("Other");

            if (!validGender) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Gender must be Male, Female or Other"
                );
            }
        }

        // Address
        if (request.getAddress() != null &&
                request.getAddress().length() > 500) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Address cannot exceed 500 characters"
            );
        }
    }

    private ProfileResponse convertToResponse(
            User user) {

        return new ProfileResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getPhone(),
                user.getDateOfBirth(),
                user.getGender(),
                user.getAddress(),
                user.getRole() != null
                        ? user.getRole().name()
                        : null,
                user.getStatus() != null
                        ? user.getStatus().name()
                        : null,
                user.getCreatedAt()
        );
    }
}