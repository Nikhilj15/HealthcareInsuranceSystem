package com.healthcare.healthcare_insurance_api.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.healthcare.healthcare_insurance_api.entity.User;
import com.healthcare.healthcare_insurance_api.repository.UserRepository;

@Component
public class UserPasswordReset implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserPasswordReset(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

        String userEmail = "user@healthcare.com";
        String newPassword = "User@123";

        User user = userRepository
                .findByEmail(userEmail)
                .orElse(null);

        if (user == null) {
            System.out.println("USER PASSWORD RESET: User not found.");
            return;
        }

        String encodedPassword =
                passwordEncoder.encode(newPassword);

        user.setPasswordHash(encodedPassword);

        userRepository.save(user);

        System.out.println("========================================");
        System.out.println("USER PASSWORD RESET SUCCESSFUL");
        System.out.println("Email    : " + userEmail);
        System.out.println("Password : User@123");
        System.out.println("Role     : " + user.getRole());
        System.out.println("========================================");
    }
}