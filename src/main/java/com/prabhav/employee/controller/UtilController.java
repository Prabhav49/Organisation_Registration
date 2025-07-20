package com.prabhav.employee.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/util")
@RequiredArgsConstructor
public class UtilController {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/hash-password")
    public String hashPassword(@RequestBody String password) {
        return passwordEncoder.encode(password.trim());
    }
    
    @GetMapping("/hash/{password}")
    public String hashPasswordGet(@PathVariable String password) {
        return passwordEncoder.encode(password);
    }
}
