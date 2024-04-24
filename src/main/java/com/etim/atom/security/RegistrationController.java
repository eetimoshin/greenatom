package com.etim.atom.security;

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

    @PostMapping("/registration")
    public ResponseEntity<?> registration(@RequestBody MyUser user) {
        return userDetailsService.save(user);
    }
}
