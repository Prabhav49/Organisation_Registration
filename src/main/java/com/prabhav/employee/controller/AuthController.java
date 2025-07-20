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
    public ResponseEntity<Map<String, Object>> login(
            @RequestBody @Valid LoginRequest request,
            HttpServletRequest httpRequest) {

        AuthService.AuthenticationResult result = authService.login(
                request.getEmail(),
                request.getPassword(),
                httpRequest
        );

        if (result.isSuccess()) {
            return ResponseEntity.ok(Map.of(
                    "token", result.getToken(),
                    "sessionId", result.getSessionId(),
                    "role", result.getRole().getRoleName(),
                    "message", result.getMessage()
            ));
        } else if (result.isRequiresTwoFactor()) {
            return ResponseEntity.ok(Map.of(
                    "requiresTwoFactor", true,
                    "message", result.getMessage()
            ));
        } else {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", result.getMessage()
            ));
        }
    }

    @PostMapping("/login/2fa")
    public ResponseEntity<Map<String, Object>> loginWithTwoFactor(
            @RequestBody Map<String, String> request,
            HttpServletRequest httpRequest) {

        String email = request.get("email");
        String password = request.get("password");
        String twoFactorCode = request.get("twoFactorCode");

        AuthService.AuthenticationResult result = authService.loginWithTwoFactor(
                email, password, twoFactorCode, httpRequest
        );

        if (result.isSuccess()) {
            return ResponseEntity.ok(Map.of(
                    "token", result.getToken(),
                    "sessionId", result.getSessionId(),
                    "role", result.getRole().getRoleName(),
                    "message", result.getMessage()
            ));
        } else {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", result.getMessage()
            ));
        }
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

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(
            @RequestBody Map<String, String> request,
            HttpServletRequest httpRequest) {

        String token = httpRequest.getHeader("Authorization");
        if (jwtUtil.validateToken(token)) {
            String email = jwtUtil.extractEmail(token.substring(7));
            String sessionId = request.get("sessionId");
            authService.logout(email, sessionId);
        }

        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
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

    private String getTokenFromRequest(HttpServletRequest request) {
        return request.getHeader("Authorization");
    }
}
