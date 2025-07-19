package com.prabhav.employee.controller;

import com.prabhav.employee.auth.AuthService;
import com.prabhav.employee.auth.JwtUtil;
import com.prabhav.employee.dto.LoginRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody @Valid LoginRequest request) {
        String token = authService.login(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(token);
    }

    @GetMapping("/oauth2/authorize/google")
    public void authorizeGoogle(HttpServletResponse response) throws IOException {
        response.sendRedirect("/oauth2/authorization/google");
    }

    @PostMapping("/oauth2/login")
    public ResponseEntity<Map<String, String>> oauth2Login(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String provider = request.get("provider");
        
        String token = authService.processOAuth2Login(email, provider);
        
        if (token.startsWith("User not found")) {
            return ResponseEntity.badRequest().body(Map.of("error", token));
        }
        
        return ResponseEntity.ok(Map.of("token", token, "user", email));
    }

    @PostMapping("/oauth2/logout")
    public ResponseEntity<Map<String, String>> oauth2Logout(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        authService.initiateGoogleLogout(email);
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }

    @GetMapping("/test-token")
    public ResponseEntity<String> testToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        System.out.println("Test endpoint - received token: " + (token != null ? token.substring(0, Math.min(30, token.length())) + "..." : "null"));
        
        if (jwtUtil.validateToken(token)) {
            String email = jwtUtil.extractEmail(token.substring(7)); // Remove "Bearer "
            return ResponseEntity.ok("Token is valid for user: " + email);
        } else {
            return ResponseEntity.status(401).body("Token is invalid");
        }
    }
}
