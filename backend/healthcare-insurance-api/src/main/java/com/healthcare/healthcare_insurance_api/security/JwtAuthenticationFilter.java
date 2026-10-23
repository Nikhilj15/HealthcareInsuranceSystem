package com.healthcare.healthcare_insurance_api.security;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.healthcare.healthcare_insurance_api.service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String requestPath = request.getRequestURI();

        System.out.println();
        System.out.println("==============================================");
        System.out.println("JWT FILTER");
        System.out.println("Request : "
                + request.getMethod()
                + " "
                + requestPath);
        System.out.println("==============================================");

        String authHeader =
                request.getHeader("Authorization");

        System.out.println(
                "Authorization Header Present : "
                + (authHeader != null)
        );

        // =================================================
        // PUBLIC APIs
        // =================================================

        if (requestPath.equals("/api/health")
                || requestPath.equals("/api/users/register")
                || requestPath.equals("/api/users/check-email")
                || requestPath.equals("/api/auth/login")) {

            System.out.println(
                    "Public API - JWT not required"
            );

            filterChain.doFilter(request, response);
            return;
        }

        // =================================================
        // NO AUTHORIZATION HEADER
        // =================================================

        if (authHeader == null
                || !authHeader.startsWith("Bearer ")) {

            System.out.println(
                    "Authorization header missing/invalid"
            );

            filterChain.doFilter(request, response);
            return;
        }

        // =================================================
        // EXTRACT TOKEN
        // =================================================

        String token =
                authHeader.substring(7).trim();

        System.out.println(
                "JWT received"
        );

        System.out.println(
                "JWT length : "
                + token.length()
        );

        try {

            // =================================================
            // VALIDATE TOKEN
            // =================================================

            if (!jwtService.isTokenValid(token)) {

                System.out.println(
                        "JWT INVALID"
                );

                SecurityContextHolder.clearContext();

                filterChain.doFilter(
                        request,
                        response
                );

                return;
            }

            System.out.println(
                    "JWT VALID"
            );

            // =================================================
            // EXTRACT EMAIL
            // =================================================

            String email =
                    jwtService.extractEmail(token);

            // =================================================
            // EXTRACT ROLE
            // =================================================

            String role =
                    jwtService.extractRole(token);

            System.out.println(
                    "Email : "
                    + email
            );

            System.out.println(
                    "Raw Role from JWT : "
                    + role
            );

            // =================================================
            // ROLE VALIDATION
            // =================================================

            if (role == null
                    || role.trim().isEmpty()) {

                System.out.println(
                        "ERROR: JWT ROLE IS MISSING"
                );

                SecurityContextHolder.clearContext();

                filterChain.doFilter(
                        request,
                        response
                );

                return;
            }

            // =================================================
            // NORMALIZE ROLE
            // =================================================

            role = role.trim().toUpperCase();

            if (!role.startsWith("ROLE_")) {
                role = "ROLE_" + role;
            }

            System.out.println(
                    "Spring Security Authority : "
                    + role
            );

            // =================================================
            // CREATE AUTHORITY
            // =================================================

            SimpleGrantedAuthority authority =
                    new SimpleGrantedAuthority(role);

            // =================================================
            // CREATE AUTHENTICATION
            // =================================================

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            email,
                            null,
                            List.of(authority)
                    );

            authentication.setDetails(
                    new WebAuthenticationDetailsSource()
                            .buildDetails(request)
            );

            // =================================================
            // STORE AUTHENTICATION
            // =================================================

            SecurityContextHolder
                    .getContext()
                    .setAuthentication(authentication);

            System.out.println(
                    "Authentication successfully set"
            );

            System.out.println(
                    "Authenticated User : "
                    + email
            );

            System.out.println(
                    "Authorities : "
                    + authentication.getAuthorities()
            );

        } catch (Exception e) {

            SecurityContextHolder.clearContext();

            System.out.println(
                    "JWT Authentication failed"
            );

            System.out.println(
                    "Exception : "
                    + e.getClass().getSimpleName()
            );

            System.out.println(
                    "Message : "
                    + e.getMessage()
            );
        }

        // =================================================
        // FINAL AUTHENTICATION CHECK
        // =================================================

        System.out.println(
                "Authentication before controller : "
                + SecurityContextHolder
                        .getContext()
                        .getAuthentication()
        );

        System.out.println(
                "=============================================="
        );

        filterChain.doFilter(
                request,
                response
        );
    }
}