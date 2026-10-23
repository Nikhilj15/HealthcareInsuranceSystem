package com.healthcare.healthcare_insurance_api.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.healthcare.healthcare_insurance_api.entity.User;
import com.healthcare.healthcare_insurance_api.repository.UserRepository;

@Component
public class AdminPasswordReset implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminPasswordReset(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

        String adminEmail = "sanika@gmail.com";
        String newPassword = "Admin@123";

        User admin = userRepository
                .findByEmail(adminEmail)
                .orElse(null);

        if (admin == null) {

            System.out.println(
                "ADMIN PASSWORD RESET: Admin user not found."
            );

            return;
        }

        String encodedPassword =
                passwordEncoder.encode(newPassword);

        admin.setPasswordHash(encodedPassword);

        userRepository.save(admin);

        System.out.println(
            "========================================"
        );

        System.out.println(
            "ADMIN PASSWORD RESET SUCCESSFUL"
        );

        System.out.println(
            "Email : " + adminEmail
        );

        System.out.println(
            "Password : Admin@123"
        );

        System.out.println(
            "========================================"
        );
    }
}