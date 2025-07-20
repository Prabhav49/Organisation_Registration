package com.prabhav.employee.controller;

import com.prabhav.employee.auth.AuthService;
import com.prabhav.employee.auth.JwtUtil;
import com.prabhav.employee.dto.PasswordPolicyRequest;
import com.prabhav.employee.dto.TwoFactorRequest;
import com.prabhav.employee.dto.TwoFactorResponse;
import com.prabhav.employee.entity.Role;
import com.prabhav.employee.service.PasswordPolicyService;
import com.prabhav.employee.service.TwoFactorAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/security")
@RequiredArgsConstructor
public class SecurityController {

    private final PasswordPolicyService passwordPolicyService;
    private final TwoFactorAuthService twoFactorAuthService;
    private final AuthService authService;
    private final JwtUtil jwtUtil;

    @PostMapping("/password/change")
    public ResponseEntity<Map<String, String>> changePassword(
            @Valid @RequestBody PasswordPolicyRequest request,
            HttpServletRequest httpRequest) {
        
        String token = getTokenFromRequest(httpRequest);
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

        String email = jwtUtil.extractEmail(token.substring(7));
        String result = passwordPolicyService.changePasswordWithPolicy(email, request);
        
        if ("Password changed successfully".equals(result)) {
            return ResponseEntity.ok(Map.of("message", result));
        } else {
            return ResponseEntity.badRequest().body(Map.of("error", result));
        }
    }

    @PostMapping("/2fa/setup")
    public ResponseEntity<TwoFactorResponse> setupTwoFactor(HttpServletRequest httpRequest) {
        String token = getTokenFromRequest(httpRequest);
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(401).body(
                TwoFactorResponse.builder().message("Unauthorized").build()
            );
        }

        String email = jwtUtil.extractEmail(token.substring(7));
        TwoFactorResponse response = twoFactorAuthService.setupTwoFactor(email);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/2fa/enable")
    public ResponseEntity<TwoFactorResponse> enableTwoFactor(
            @Valid @RequestBody TwoFactorRequest request,
            HttpServletRequest httpRequest) {
        
        String token = getTokenFromRequest(httpRequest);
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(401).body(
                TwoFactorResponse.builder().message("Unauthorized").build()
            );
        }

        String email = jwtUtil.extractEmail(token.substring(7));
        request.setEmail(email);
        TwoFactorResponse response = twoFactorAuthService.enableTwoFactor(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/2fa/status")
    public ResponseEntity<Map<String, Object>> getTwoFactorStatus(HttpServletRequest httpRequest) {
        String token = getTokenFromRequest(httpRequest);
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

        String email = jwtUtil.extractEmail(token.substring(7));
        boolean isEnabled = twoFactorAuthService.isTwoFactorEnabled(email);
        return ResponseEntity.ok(Map.of("isEnabled", isEnabled));
    }

    @PostMapping("/2fa/disable")
    public ResponseEntity<TwoFactorResponse> disableTwoFactor(
            @Valid @RequestBody TwoFactorRequest request,
            HttpServletRequest httpRequest) {
        
        String token = getTokenFromRequest(httpRequest);
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(401).body(
                TwoFactorResponse.builder().message("Unauthorized").build()
            );
        }

        String email = jwtUtil.extractEmail(token.substring(7));
        request.setEmail(email);
        TwoFactorResponse response = twoFactorAuthService.disableTwoFactor(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/2fa/verify")
    public ResponseEntity<Map<String, Object>> verifyTwoFactor(
            @Valid @RequestBody TwoFactorRequest request) {
        
        boolean isValid = twoFactorAuthService.verifyTwoFactorCode(request.getEmail(), request.getVerificationCode());
        
        if (isValid) {
            return ResponseEntity.ok(Map.of(
                "valid", true,
                "message", "Two-factor authentication verified successfully"
            ));
        } else {
            return ResponseEntity.badRequest().body(Map.of(
                "valid", false,
                "message", "Invalid two-factor authentication code"
            ));
        }
    }

    @PostMapping("/unlock-account")
    public ResponseEntity<Map<String, String>> unlockAccount(
            @RequestBody Map<String, String> request,
            HttpServletRequest httpRequest) {
        
        String token = getTokenFromRequest(httpRequest);
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

        // Check if user has admin privileges
        if (!jwtUtil.hasAnyRole(token.substring(7), Role.ADMIN, Role.SUPER_ADMIN)) {
            return ResponseEntity.status(403).body(Map.of("error", "Insufficient privileges"));
        }

        String adminEmail = jwtUtil.extractEmail(token.substring(7));
        String userEmail = request.get("userEmail");
        
        authService.unlockAccount(adminEmail, userEmail);
        return ResponseEntity.ok(Map.of("message", "Account unlocked successfully"));
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        return request.getHeader("Authorization");
    }
}
