package com.prabhav.employee.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "two_factor_auth")
public class TwoFactorAuth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_email", nullable = false)
    private String userEmail;

    @Column(name = "secret_key")
    private String secretKey;

    @Column(name = "backup_codes", columnDefinition = "TEXT")
    private String backupCodes; // JSON array of backup codes

    @Column(name = "is_enabled")
    @Builder.Default
    private Boolean isEnabled = false;

    @Column(name = "verification_code")
    private String verificationCode;

    @Column(name = "code_expiry")
    private LocalDateTime codeExpiry;

    @Column(name = "created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
