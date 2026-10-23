package com.healthcare.healthcare_insurance_api.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/protected")
    public String protectedTest(
            Authentication authentication) {

        return "Protected API working. "
                + "Logged in user: "
                + authentication.getName();
    }
}