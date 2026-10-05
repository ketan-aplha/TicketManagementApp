package com.Library.Management.controller;

import com.Library.Management.dto.RegisterRequest;
import com.Library.Management.service.OrganisationService;
import com.Library.Management.service.UserService;
import jakarta.validation.constraints.NotBlank;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/organization")
public class OrganizationController {
    private static final Logger log = LoggerFactory.getLogger(OrganizationController.class);
    private  final OrganisationService organisationService;

    @PostMapping()
    public ResponseEntity<?> register(
            @NonNull @NotBlank @RequestBody String name) {
        log.info("Received request to register organization {}", name);
        organisationService.create(name);
        return new ResponseEntity<>(HttpStatusCode.valueOf(201));
    }
}
