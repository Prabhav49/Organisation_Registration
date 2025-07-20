package com.prabhav.employee.service;

import com.prabhav.employee.dto.TwoFactorRequest;
import com.prabhav.employee.dto.TwoFactorResponse;
import com.prabhav.employee.entity.Employee;
import com.prabhav.employee.entity.TwoFactorAuth;
import com.prabhav.employee.repo.EmployeeRepo;
import com.prabhav.employee.repo.TwoFactorAuthRepo;
import dev.samstevens.totp.code.CodeGenerator;
import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.exceptions.QrGenerationException;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TwoFactorAuthService {

    private final TwoFactorAuthRepo twoFactorAuthRepo;
    private final EmployeeRepo employeeRepo;
    private final AuditService auditService;

    private final SecretGenerator secretGenerator = new DefaultSecretGenerator();
    private final QrGenerator qrGenerator = new ZxingPngQrGenerator();
    private final CodeGenerator codeGenerator = new DefaultCodeGenerator();
    private final TimeProvider timeProvider = new SystemTimeProvider();
    private final CodeVerifier codeVerifier = new DefaultCodeVerifier(codeGenerator, timeProvider);

    public TwoFactorResponse setupTwoFactor(String email) {
        Employee employee = employeeRepo.findByEmail(email);
        if (employee == null) {
            return TwoFactorResponse.builder()
                    .message("Employee not found")
                    .build();
        }

        try {
            // Generate secret key
            String secret = secretGenerator.generate();
            
            // Generate backup codes
            List<String> backupCodes = generateBackupCodes();
            
            // Generate QR code
            QrData data = new QrData.Builder()
                    .label(email)
                    .secret(secret)
                    .issuer("Organization Registration System")
                    .algorithm(dev.samstevens.totp.code.HashingAlgorithm.SHA1)
                    .digits(6)
                    .period(30)
                    .build();
            
            String qrCodeImage = Base64.getEncoder().encodeToString(qrGenerator.generate(data));
            
            // Save or update 2FA settings
            Optional<TwoFactorAuth> existingAuth = twoFactorAuthRepo.findByUserEmail(email);
            TwoFactorAuth twoFactorAuth;
            
            if (existingAuth.isPresent()) {
                twoFactorAuth = existingAuth.get();
                twoFactorAuth.setSecretKey(secret);
                twoFactorAuth.setBackupCodes(String.join(",", backupCodes));
                twoFactorAuth.setIsEnabled(false); // Will be enabled after verification
                twoFactorAuth.setUpdatedAt(LocalDateTime.now());
            } else {
                twoFactorAuth = TwoFactorAuth.builder()
                        .userEmail(email)
                        .secretKey(secret)
                        .backupCodes(String.join(",", backupCodes))
                        .isEnabled(false)
                        .build();
            }
            
            twoFactorAuthRepo.save(twoFactorAuth);
            
            auditService.logAction(email, "SETUP_2FA", "TwoFactorAuth", email, 
                                 null, "2FA setup initiated", "SUCCESS");
            
            return TwoFactorResponse.builder()
                    .secretKey(secret)
                    .qrCodeUrl("data:image/png;base64," + qrCodeImage)
                    .backupCodes(backupCodes)
                    .isEnabled(false)
                    .message("2FA setup successful. Please verify with your authenticator app.")
                    .build();
                    
        } catch (QrGenerationException e) {
            log.error("Error generating QR code for 2FA setup", e);
            auditService.logFailedAction(email, "SETUP_2FA", "TwoFactorAuth", email, 
                                       "QR code generation failed");
            return TwoFactorResponse.builder()
                    .message("Error generating QR code")
                    .build();
        }
    }

    public TwoFactorResponse enableTwoFactor(TwoFactorRequest request) {
        Optional<TwoFactorAuth> authOpt = twoFactorAuthRepo.findByUserEmail(request.getEmail());
        if (authOpt.isEmpty()) {
            return TwoFactorResponse.builder()
                    .message("2FA not set up for this user")
                    .build();
        }

        TwoFactorAuth twoFactorAuth = authOpt.get();
        
        if (verifyCode(twoFactorAuth.getSecretKey(), request.getVerificationCode())) {
            twoFactorAuth.setIsEnabled(true);
            twoFactorAuth.setUpdatedAt(LocalDateTime.now());
            twoFactorAuthRepo.save(twoFactorAuth);
            
            // Update employee record
            Employee employee = employeeRepo.findByEmail(request.getEmail());
            if (employee != null) {
                employee.setIsTwoFactorEnabled(true);
                employeeRepo.save(employee);
            }
            
            auditService.logAction(request.getEmail(), "ENABLE_2FA", "TwoFactorAuth", 
                                 request.getEmail(), "2FA disabled", "2FA enabled", "SUCCESS");
            
            return TwoFactorResponse.builder()
                    .isEnabled(true)
                    .message("Two-factor authentication enabled successfully")
                    .build();
        } else {
            auditService.logFailedAction(request.getEmail(), "ENABLE_2FA", "TwoFactorAuth", 
                                       request.getEmail(), "Invalid verification code");
            return TwoFactorResponse.builder()
                    .isEnabled(false)
                    .message("Invalid verification code")
                    .build();
        }
    }

    public TwoFactorResponse disableTwoFactor(TwoFactorRequest request) {
        Optional<TwoFactorAuth> authOpt = twoFactorAuthRepo.findByUserEmail(request.getEmail());
        if (authOpt.isEmpty()) {
            return TwoFactorResponse.builder()
                    .message("2FA not found for this user")
                    .build();
        }

        TwoFactorAuth twoFactorAuth = authOpt.get();
        
        if (verifyCode(twoFactorAuth.getSecretKey(), request.getVerificationCode()) || 
            verifyBackupCode(twoFactorAuth, request.getVerificationCode())) {
            
            twoFactorAuth.setIsEnabled(false);
            twoFactorAuth.setUpdatedAt(LocalDateTime.now());
            twoFactorAuthRepo.save(twoFactorAuth);
            
            // Update employee record
            Employee employee = employeeRepo.findByEmail(request.getEmail());
            if (employee != null) {
                employee.setIsTwoFactorEnabled(false);
                employeeRepo.save(employee);
            }
            
            auditService.logAction(request.getEmail(), "DISABLE_2FA", "TwoFactorAuth", 
                                 request.getEmail(), "2FA enabled", "2FA disabled", "SUCCESS");
            
            return TwoFactorResponse.builder()
                    .isEnabled(false)
                    .message("Two-factor authentication disabled successfully")
                    .build();
        } else {
            auditService.logFailedAction(request.getEmail(), "DISABLE_2FA", "TwoFactorAuth", 
                                       request.getEmail(), "Invalid verification code");
            return TwoFactorResponse.builder()
                    .message("Invalid verification code")
                    .build();
        }
    }

    public void disableUserTwoFactor(String email) {
        Optional<TwoFactorAuth> authOpt = twoFactorAuthRepo.findByUserEmail(email);
        if (authOpt.isPresent()) {
            TwoFactorAuth twoFactorAuth = authOpt.get();
            twoFactorAuth.setIsEnabled(false);
            twoFactorAuthRepo.save(twoFactorAuth);
        }
    }

    public boolean verifyTwoFactorCode(String email, String code) {
        Optional<TwoFactorAuth> authOpt = twoFactorAuthRepo.findByUserEmail(email);
        if (authOpt.isEmpty() || !authOpt.get().getIsEnabled()) {
            return false;
        }

        TwoFactorAuth twoFactorAuth = authOpt.get();
        
        // Verify TOTP code or backup code
        boolean isValid = verifyCode(twoFactorAuth.getSecretKey(), code) || 
                         verifyBackupCode(twoFactorAuth, code);
        
        if (isValid) {
            auditService.logAction(email, "VERIFY_2FA", "TwoFactorAuth", email, 
                                 null, "2FA verification successful", "SUCCESS");
        } else {
            auditService.logFailedAction(email, "VERIFY_2FA", "TwoFactorAuth", email, 
                                       "Invalid 2FA code");
        }
        
        return isValid;
    }

    public boolean isTwoFactorEnabled(String email) {
        // Check Employee table for consistency with login flow
        Employee employee = employeeRepo.findByEmail(email);
        return employee != null && employee.getIsTwoFactorEnabled();
    }

    private boolean verifyCode(String secret, String code) {
        return codeVerifier.isValidCode(secret, code);
    }

    private boolean verifyBackupCode(TwoFactorAuth twoFactorAuth, String code) {
        if (twoFactorAuth.getBackupCodes() == null || twoFactorAuth.getBackupCodes().isEmpty()) {
            return false;
        }
        
        String[] codes = twoFactorAuth.getBackupCodes().split(",");
        for (int i = 0; i < codes.length; i++) {
            if (codes[i].equals(code)) {
                // Remove used backup code
                List<String> remainingCodes = new ArrayList<>();
                for (int j = 0; j < codes.length; j++) {
                    if (j != i) {
                        remainingCodes.add(codes[j]);
                    }
                }
                twoFactorAuth.setBackupCodes(String.join(",", remainingCodes));
                twoFactorAuth.setUpdatedAt(LocalDateTime.now());
                twoFactorAuthRepo.save(twoFactorAuth);
                return true;
            }
        }
        return false;
    }

    private List<String> generateBackupCodes() {
        List<String> codes = new ArrayList<>();
        SecureRandom random = new SecureRandom();
        
        for (int i = 0; i < 10; i++) {
            int code = 100000 + random.nextInt(900000); // 6-digit codes
            codes.add(String.valueOf(code));
        }
        
        return codes;
    }
}
