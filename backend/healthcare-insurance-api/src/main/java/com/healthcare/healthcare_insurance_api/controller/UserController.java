package com.healthcare.healthcare_insurance_api.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.healthcare.healthcare_insurance_api.dto.RegisterRequest;
import com.healthcare.healthcare_insurance_api.dto.UserResponse;
import com.healthcare.healthcare_insurance_api.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;


    // Constructor

    public UserController(UserService userService) {

        this.userService = userService;
    }


    // ==============================
    // REGISTER USER
    // ==============================

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(

            @Valid
            @RequestBody
            RegisterRequest request) {


        UserResponse response =
            userService.register(request);


        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    // ==============================
    // CHECK EMAIL
    // ==============================

    @GetMapping("/check-email")
    public ResponseEntity<Map<String, Boolean>> checkEmail(

            @RequestParam
            String email) {


        boolean exists =
            userService.emailExists(email);


        return ResponseEntity.ok(
            Map.of(
                "exists",
                exists
            )
        );
    }
}