package com.prabhav.employee.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "employee")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long employeeId;

    @Column(name = "first_name", nullable = false)
    @NotBlank(message = "First name is mandatory")
    @Size(max = 50, message = "First name must not exceed 50 characters")
    private String firstName;

    @Column(name = "last_name")
    @Size(max = 50, message = "Last name must not exceed 50 characters")
    private String lastName;

    @Column(name = "email", nullable = false, unique = true)
    @NotBlank(message = "Email is mandatory")
    @Email(message = "Invalid email format")
    private String email;

    @Column(name = "password", nullable = true)
    private String password;

    @Column(name = "title")
    @Size(max = 100, message = "Title must not exceed 100 characters")
    private String title;

    @Column(name = "photograph_path")
    private String photographPath;

    @Column(name = "department_id")
    private Long departmentId;

    // OAuth2 fields
    @Column(name = "oauth_provider")
    @Builder.Default
    private String oauthProvider = "LOCAL";

    @Column(name = "oauth_id")
    private String oauthId;

    @Column(name = "is_email_verified")
    @Builder.Default
    private Boolean isEmailVerified = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    @Builder.Default
    private Role role = Role.EMPLOYEE;

    @Column(name = "is_account_locked")
    @Builder.Default
    private Boolean isAccountLocked = false;

    @Column(name = "failed_login_attempts")
    @Builder.Default
    private Integer failedLoginAttempts = 0;

    @Column(name = "last_login")
    private java.time.LocalDateTime lastLogin;

    @Column(name = "password_changed_at")
    private java.time.LocalDateTime passwordChangedAt;

    @Column(name = "is_two_factor_enabled")
    @Builder.Default
    private Boolean isTwoFactorEnabled = false;

    @Column(name = "created_at")
    @Builder.Default
    private java.time.LocalDateTime createdAt = java.time.LocalDateTime.now();

    @Column(name = "updated_at")
    @Builder.Default
    private java.time.LocalDateTime updatedAt = java.time.LocalDateTime.now();

    @PrePersist
    public void prePersist() {
        if (this.photographPath == null || this.photographPath.isEmpty()) {
            this.photographPath = "/uploads/images/default.png"; // Default profile picture
        }
        
        if (this.oauthProvider == null) {
            this.oauthProvider = "LOCAL";
        }
        
        if (this.isEmailVerified == null) {
            this.isEmailVerified = false;
        }
        
        if (this.role == null) {
            this.role = Role.EMPLOYEE;
        }
        
        if (this.isAccountLocked == null) {
            this.isAccountLocked = false;
        }
        
        if (this.failedLoginAttempts == null) {
            this.failedLoginAttempts = 0;
        }
        
        if (this.isTwoFactorEnabled == null) {
            this.isTwoFactorEnabled = false;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = java.time.LocalDateTime.now();
    }
}
