package com.Library.Management.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @GetMapping("/me")
    public String getCurrentUser(
            Authentication authentication) {
        log.info("Received request for current user {}", authentication.getName());
        return "Logged in as: "
                + authentication.getName();
    }

    @GetMapping("/hello")
    public String hello() {
        log.info("Received hello request from authenticated user");
        return "Hello authenticated user!";
    }
}
