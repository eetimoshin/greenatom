package com.etim.atom.controllers;

import com.etim.atom.security.MyUser;
import com.etim.atom.services.MyUserDetailsService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
public class RegistrationController {

    private final MyUserDetailsService userDetailsService;

    @Operation(summary = "Register a user")
    @PostMapping("/registration")
    public ResponseEntity<?> registration(@RequestBody MyUser user) {
        return userDetailsService.save(user);
    }
}
