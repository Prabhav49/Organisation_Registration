package com.prabhav.employee.service;

import com.prabhav.employee.dto.PasswordPolicyRequest;
import com.prabhav.employee.entity.Employee;
import com.prabhav.employee.entity.PasswordHistory;
import com.prabhav.employee.repo.EmployeeRepo;
import com.prabhav.employee.repo.PasswordHistoryRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class PasswordPolicyService {

    private final PasswordHistoryRepo passwordHistoryRepo;
    private final EmployeeRepo employeeRepo;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final AuditService auditService;

    // Password policy constants
    private static final int MIN_LENGTH = 8;
    private static final int MAX_LENGTH = 50;
    private static final int PASSWORD_HISTORY_LIMIT = 5; // Don't allow reuse of last 5 passwords
    private static final Pattern UPPERCASE_PATTERN = Pattern.compile(".*[A-Z].*");
    private static final Pattern LOWERCASE_PATTERN = Pattern.compile(".*[a-z].*");
    private static final Pattern DIGIT_PATTERN = Pattern.compile(".*\\d.*");
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile(".*[@$!%*?&].*");

    public String changePasswordWithPolicy(String email, PasswordPolicyRequest request) {
        Employee employee = employeeRepo.findByEmail(email);
        if (employee == null) {
            auditService.logFailedAction(email, "CHANGE_PASSWORD", "Employee", email, "Employee not found");
            return "Employee not found";
        }

        // Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), employee.getPassword())) {
            auditService.logFailedAction(email, "CHANGE_PASSWORD", "Employee", email, "Invalid current password");
            return "Current password is incorrect";
        }

        // Check if new password matches confirm password
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            auditService.logFailedAction(email, "CHANGE_PASSWORD", "Employee", email, "Password confirmation mismatch");
            return "New password and confirm password do not match";
        }

        // Validate password policy
        String validationResult = validatePasswordPolicy(request.getNewPassword());
        if (!validationResult.equals("VALID")) {
            auditService.logFailedAction(email, "CHANGE_PASSWORD", "Employee", email, validationResult);
            return validationResult;
        }

        // Check password history
        if (isPasswordInHistory(email, request.getNewPassword())) {
            auditService.logFailedAction(email, "CHANGE_PASSWORD", "Employee", email, "Password recently used");
            return "Password has been used recently. Please choose a different password.";
        }

        // Save old password to history
        savePasswordToHistory(email, employee.getPassword());

        // Update password
        employee.setPassword(passwordEncoder.encode(request.getNewPassword()));
        employee.setPasswordChangedAt(LocalDateTime.now());
        employeeRepo.save(employee);

        auditService.logAction(email, "CHANGE_PASSWORD", "Employee", email, 
                             "Password changed", "Password updated", "SUCCESS");

        return "Password changed successfully";
    }

    public String validatePasswordPolicy(String password) {
        if (password == null || password.trim().isEmpty()) {
            return "Password cannot be empty";
        }

        if (password.length() < MIN_LENGTH) {
            return "Password must be at least " + MIN_LENGTH + " characters long";
        }

        if (password.length() > MAX_LENGTH) {
            return "Password must not exceed " + MAX_LENGTH + " characters";
        }

        if (!UPPERCASE_PATTERN.matcher(password).matches()) {
            return "Password must contain at least one uppercase letter";
        }

        if (!LOWERCASE_PATTERN.matcher(password).matches()) {
            return "Password must contain at least one lowercase letter";
        }

        if (!DIGIT_PATTERN.matcher(password).matches()) {
            return "Password must contain at least one digit";
        }

        if (!SPECIAL_CHAR_PATTERN.matcher(password).matches()) {
            return "Password must contain at least one special character (@$!%*?&)";
        }

        return "VALID";
    }

    private boolean isPasswordInHistory(String email, String newPassword) {
        List<PasswordHistory> passwordHistory = passwordHistoryRepo
                .findTopNByUserEmailOrderByCreatedAtDesc(email, PASSWORD_HISTORY_LIMIT);
        
        return passwordHistory.stream()
                .anyMatch(ph -> passwordEncoder.matches(newPassword, ph.getPasswordHash()));
    }

    private void savePasswordToHistory(String email, String passwordHash) {
        PasswordHistory passwordHistory = PasswordHistory.builder()
                .userEmail(email)
                .passwordHash(passwordHash)
                .createdAt(LocalDateTime.now())
                .build();
        
        passwordHistoryRepo.save(passwordHistory);
    }

    public boolean isPasswordExpired(Employee employee) {
        if (employee.getPasswordChangedAt() == null) {
            return true; // Force password change if never changed
        }
        
        // Password expires after 90 days
        LocalDateTime expiryDate = employee.getPasswordChangedAt().plusDays(90);
        return LocalDateTime.now().isAfter(expiryDate);
    }

    public int getDaysUntilPasswordExpiry(Employee employee) {
        if (employee.getPasswordChangedAt() == null) {
            return 0;
        }
        
        LocalDateTime expiryDate = employee.getPasswordChangedAt().plusDays(90);
        LocalDateTime now = LocalDateTime.now();
        
        if (now.isAfter(expiryDate)) {
            return 0;
        }
        
        return (int) java.time.Duration.between(now, expiryDate).toDays();
    }
}
