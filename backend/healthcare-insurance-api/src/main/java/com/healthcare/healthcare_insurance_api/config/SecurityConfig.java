package com.healthcare.healthcare_insurance_api.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.healthcare.healthcare_insurance_api.security.JwtAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter) {

        this.jwtAuthenticationFilter =
                jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http

            // ==========================================
            // CORS
            // ==========================================

            .cors(cors ->
                    cors.configurationSource(
                            corsConfigurationSource()
                    )
            )

            // ==========================================
            // CSRF
            // ==========================================

            .csrf(csrf -> csrf.disable())

            // ==========================================
            // STATELESS JWT
            // ==========================================

            .sessionManagement(session ->
                    session.sessionCreationPolicy(
                            SessionCreationPolicy.STATELESS
                    )
            )

            // ==========================================
            // AUTHORIZATION
            // ==========================================

            .authorizeHttpRequests(auth -> auth

                // PUBLIC AUTH
                .requestMatchers(
                        "/api/auth/**"
                ).permitAll()

                .requestMatchers(
                        "/api/users/register",
                        "/api/users/check-email"
                ).permitAll()

                .requestMatchers(
                        "/api/health"
                ).permitAll()

             // ======================================
             // POLICY PLANS
             // ======================================

             // Public visitors can view available plans
             .requestMatchers(
                     HttpMethod.GET,
                     "/api/policy-plans",
                     "/api/policy-plans/**"
             ).permitAll()

             // Only admins can create plans
             .requestMatchers(
                     HttpMethod.POST,
                     "/api/policy-plans",
                     "/api/policy-plans/**"
             ).hasRole("ADMIN")

             // Only admins can update plans
             .requestMatchers(
                     HttpMethod.PUT,
                     "/api/policy-plans",
                     "/api/policy-plans/**"
             ).hasRole("ADMIN")

             // Only admins can delete plans
             .requestMatchers(
                     HttpMethod.DELETE,
                     "/api/policy-plans",
                     "/api/policy-plans/**"
             ).hasRole("ADMIN")

                // ======================================
                // POLICY PURCHASE
                // ======================================

                .requestMatchers(
                        HttpMethod.POST,
                        "/api/policies/purchase"
                ).hasRole("USER")

                // Keep compatibility with old URL temporarily
                .requestMatchers(
                        HttpMethod.POST,
                        "/api/policies/purchase/**"
                ).hasRole("USER")

                // ======================================
                // USER POLICIES
                // ======================================

                .requestMatchers(
                        HttpMethod.GET,
                        "/api/policies/user/**"
                ).hasRole("USER")

                .requestMatchers(
                        HttpMethod.GET,
                        "/api/policies/*"
                ).hasAnyRole("USER", "ADMIN")

                // ======================================
                // ADMIN POLICIES
                // ======================================

                .requestMatchers(
                        HttpMethod.GET,
                        "/api/policies"
                ).hasRole("ADMIN")

                .requestMatchers(
                        HttpMethod.PUT,
                        "/api/policies/*/approve"
                ).hasRole("ADMIN")

                .requestMatchers(
                        HttpMethod.PUT,
                        "/api/policies/*/reject"
                ).hasRole("ADMIN")

                // ======================================
                // ADMIN APIs
                // ======================================

                .requestMatchers(
                        "/api/admin/**"
                ).hasRole("ADMIN")

                // ======================================
                // EVERYTHING ELSE
                // ======================================

                .anyRequest().authenticated()
            )

            .addFilterBefore(
                    jwtAuthenticationFilter,
                    UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }

    // =================================================
    // CORS
    // =================================================

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration =
                new CorsConfiguration();

        configuration.setAllowedOrigins(
                List.of(
                        "http://localhost:4200"
                )
        );

        configuration.setAllowedMethods(
                List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "DELETE",
                        "PATCH",
                        "OPTIONS"
                )
        );

        configuration.setAllowedHeaders(
                List.of("*")
        );

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                configuration
        );

        return source;
    }

    // =================================================
    // PASSWORD ENCODER
    // =================================================

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }
}