package com.healthcare.healthcare_insurance_api.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.healthcare.healthcare_insurance_api.dto.LoginRequest;
import com.healthcare.healthcare_insurance_api.dto.LoginResponse;
import com.healthcare.healthcare_insurance_api.dto.RegisterRequest;
import com.healthcare.healthcare_insurance_api.entity.User;
import com.healthcare.healthcare_insurance_api.enums.Role;
import com.healthcare.healthcare_insurance_api.enums.UserStatus;
import com.healthcare.healthcare_insurance_api.repository.UserRepository;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    // LOGIN

    public LoginResponse login(LoginRequest request) {

        if (request == null) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Login data is required"
            );
        }

        if (request.getEmail() == null
                || request.getEmail().trim().isEmpty()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Email is required"
            );
        }

        if (request.getPassword() == null
                || request.getPassword().isEmpty()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Password is required"
            );
        }

        if (request.getLoginType() == null
                || !(request.getLoginType().equals("USER")
                || request.getLoginType().equals("ADMIN"))) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Please select a valid login type"
            );
        }

        String email = request.getEmail()
                .trim()
                .toLowerCase();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                    new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Invalid email or password"
                    )
                );

        // Verify password before granting any access.
        boolean passwordMatches = passwordEncoder.matches(
            request.getPassword(),
            user.getPasswordHash()
        );

        if (!passwordMatches) {
            throw new ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Invalid email or password"
            );
        }

        // Verify account status.
        if (user.getStatus() == null
                || user.getStatus() != UserStatus.ACTIVE) {
            throw new ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "Your account is not active"
            );
        }

        // CRITICAL: Compare the selected login type against
        // the actual role stored in the database.
        String actualRole = user.getRole() == null
                ? ""
                : user.getRole().name();

        if (!actualRole.equals(request.getLoginType())) {
            throw new ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "This account cannot log in through the selected "
                    + "login page. Please use the correct login."
            );
        }

        // Generate a token only after every check succeeds.
        String token = jwtService.generateToken(user);

        return new LoginResponse(
            token,
            "Bearer",
            3600,
            actualRole
        );
    }

    // REGISTER

    public String register(RegisterRequest request) {

        if (request == null) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Registration data is required"
            );
        }

        if (request.getEmail() == null
                || request.getEmail().trim().isEmpty()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Email is required"
            );
        }

        String email = request.getEmail()
                .trim()
                .toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Email is already registered"
            );
        }

        User user = new User();

        user.setFullName(request.getFullName().trim());
        user.setEmail(email);
        user.setPhone(request.getPhone().trim());

        user.setPasswordHash(
            passwordEncoder.encode(request.getPassword())
        );

        user.setDateOfBirth(request.getDateOfBirth());

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

        user.setStatus(UserStatus.ACTIVE);

        // Public registration must never create an admin.
        user.setRole(Role.USER);

        userRepository.save(user);

        return "Registration successful";
    }
}