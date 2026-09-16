package com.Library.Management.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @GetMapping("/me")
    public String getCurrentUser(
            Authentication authentication) {

        return "Logged in as: "
                + authentication.getName();
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hello authenticated user!";
    }
}
