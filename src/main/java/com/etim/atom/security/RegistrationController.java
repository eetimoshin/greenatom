package com.etim.atom.security;

import jakarta.annotation.security.PermitAll;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
public class RegistrationController {

    private final MyUserDetailsService userDetailsService;

    @PostMapping("/registration")
    public ResponseEntity<?> registration(@RequestBody MyUser user) {
        return userDetailsService.save(user);
    }
}
