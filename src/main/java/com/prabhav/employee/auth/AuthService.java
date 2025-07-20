package com.prabhav.employee.auth;

import com.prabhav.employee.entity.Employee;
import com.prabhav.employee.entity.Role;
import com.prabhav.employee.repo.EmployeeRepo;
import com.prabhav.employee.service.AuditService;
import com.prabhav.employee.service.SessionService;
import com.prabhav.employee.service.TwoFactorAuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final EmployeeRepo employeeRepo;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final AuditService auditService;
    private final SessionService sessionService;
    private final TwoFactorAuthService twoFactorAuthService;

    // Maximum failed login attempts before account lockout
    private static final int MAX_FAILED_ATTEMPTS = 5;

    public AuthenticationResult login(String email, String password, HttpServletRequest request) {
        Employee employee = employeeRepo.findByEmail(email);

        if (employee == null) {
            auditService.logFailedAction(email, "LOGIN", "Employee", email, "Employee not found");
            return AuthenticationResult.failure("Invalid email or password");
        }

        // Check if account is locked
        if (employee.getIsAccountLocked()) {
            auditService.logFailedAction(email, "LOGIN", "Employee", email, "Account locked");
            return AuthenticationResult.failure("Account is locked. Please contact administrator.");
        }

        // Verify password
        if (!passwordEncoder.matches(password, employee.getPassword())) {
            handleFailedLogin(employee, request);
            return AuthenticationResult.failure("Invalid email or password");
        }

        // Check if 2FA is enabled
        if (employee.getIsTwoFactorEnabled()) {
            // For 2FA enabled users, return a special response indicating 2FA is required
            resetFailedLoginAttempts(employee);
            return AuthenticationResult.requiresTwoFactor(email);
        }

        // Successful login
        return completeLogin(employee, request);
    }

    public AuthenticationResult loginWithTwoFactor(String email, String password, String twoFactorCode, HttpServletRequest request) {
        Employee employee = employeeRepo.findByEmail(email);

        if (employee == null) {
            auditService.logFailedAction(email, "LOGIN_2FA", "Employee", email, "Employee not found");
            return AuthenticationResult.failure("Invalid credentials");
        }

        if (employee.getIsAccountLocked()) {
            auditService.logFailedAction(email, "LOGIN_2FA", "Employee", email, "Account locked");
            return AuthenticationResult.failure("Account is locked. Please contact administrator.");
        }

        if (!passwordEncoder.matches(password, employee.getPassword())) {
            handleFailedLogin(employee, request);
            return AuthenticationResult.failure("Invalid credentials");
        }

        // Verify 2FA code
        if (!twoFactorAuthService.verifyTwoFactorCode(email, twoFactorCode)) {
            auditService.logFailedAction(email, "LOGIN_2FA", "Employee", email, "Invalid 2FA code");
            return AuthenticationResult.failure("Invalid two-factor authentication code");
        }

        return completeLogin(employee, request);
    }

    private AuthenticationResult completeLogin(Employee employee, HttpServletRequest request) {
        // Reset failed login attempts
        resetFailedLoginAttempts(employee);
        
        // Update last login time
        employee.setLastLogin(LocalDateTime.now());
        employeeRepo.save(employee);

        // Create session
        String sessionId = sessionService.createSession(employee.getEmail(), request);
        
        // Generate JWT token
        String token = jwtUtil.generateToken(employee.getEmail(), employee.getRole());

        // Log successful login
        auditService.logAction(employee.getEmail(), "LOGIN", "Employee", employee.getEmail(), 
                             null, "Successful login", "SUCCESS");

        return AuthenticationResult.success(token, sessionId, employee.getRole());
    }

    private void handleFailedLogin(Employee employee, HttpServletRequest request) {
        int failedAttempts = employee.getFailedLoginAttempts() + 1;
        employee.setFailedLoginAttempts(failedAttempts);

        if (failedAttempts >= MAX_FAILED_ATTEMPTS) {
            employee.setIsAccountLocked(true);
            auditService.logAction(employee.getEmail(), "ACCOUNT_LOCKED", "Employee", employee.getEmail(), 
                                 "Account active", "Account locked due to failed login attempts", "SUCCESS");
        }

        employeeRepo.save(employee);
        auditService.logFailedAction(employee.getEmail(), "LOGIN", "Employee", employee.getEmail(), 
                                   "Failed login attempt #" + failedAttempts);
    }

    private void resetFailedLoginAttempts(Employee employee) {
        if (employee.getFailedLoginAttempts() > 0) {
            employee.setFailedLoginAttempts(0);
            employeeRepo.save(employee);
        }
    }

    public String processOAuth2Login(String email, String provider) {
        Optional<Employee> employeeOptional = employeeRepo.findByEmailAndProviderOrLocal(email, provider);
        
        if (employeeOptional.isPresent()) {
            Employee employee = employeeOptional.get();
            employee.setLastLogin(LocalDateTime.now());
            employeeRepo.save(employee);
            
            auditService.logAction(email, "OAUTH2_LOGIN", "Employee", email, 
                                 null, "OAuth2 login successful", "SUCCESS");
            
            return jwtUtil.generateToken(email, employee.getRole());
        } else {
            auditService.logFailedAction(email, "OAUTH2_LOGIN", "Employee", email, "User not found");
            return "User not found or not authorized";
        }
    }

    public void logout(String email, String sessionId) {
        sessionService.endSession(sessionId);
        auditService.logAction(email, "LOGOUT", "Employee", email, 
                             null, "User logged out", "SUCCESS");
    }

    public void initiateGoogleLogout(String email) {
        auditService.logAction(email, "OAUTH2_LOGOUT", "Employee", email, 
                             null, "OAuth2 logout", "SUCCESS");
    }

    public void unlockAccount(String adminEmail, String userEmail) {
        Employee employee = employeeRepo.findByEmail(userEmail);
        if (employee != null && employee.getIsAccountLocked()) {
            employee.setIsAccountLocked(false);
            employee.setFailedLoginAttempts(0);
            employeeRepo.save(employee);
            
            auditService.logAction(adminEmail, "UNLOCK_ACCOUNT", "Employee", userEmail, 
                                 "Account locked", "Account unlocked", "SUCCESS");
        }
    }

    // Inner class for authentication results
    public static class AuthenticationResult {
        private final boolean success;
        private final boolean requiresTwoFactor;
        private final String token;
        private final String sessionId;
        private final String message;
        private final Role role;

        private AuthenticationResult(boolean success, boolean requiresTwoFactor, String token, 
                                   String sessionId, String message, Role role) {
            this.success = success;
            this.requiresTwoFactor = requiresTwoFactor;
            this.token = token;
            this.sessionId = sessionId;
            this.message = message;
            this.role = role;
        }

        public static AuthenticationResult success(String token, String sessionId, Role role) {
            return new AuthenticationResult(true, false, token, sessionId, "Login successful", role);
        }

        public static AuthenticationResult failure(String message) {
            return new AuthenticationResult(false, false, null, null, message, null);
        }

        public static AuthenticationResult requiresTwoFactor(String email) {
            return new AuthenticationResult(false, true, null, null, "Two-factor authentication required", null);
        }

        // Getters
        public boolean isSuccess() { return success; }
        public boolean isRequiresTwoFactor() { return requiresTwoFactor; }
        public String getToken() { return token; }
        public String getSessionId() { return sessionId; }
        public String getMessage() { return message; }
        public Role getRole() { return role; }
    }
}
