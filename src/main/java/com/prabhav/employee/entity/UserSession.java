package com.prabhav.employee.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_session")
public class UserSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_id", nullable = false, unique = true)
    private String sessionId;

    @Column(name = "user_email", nullable = false)
    private String userEmail;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "user_agent")
    private String userAgent;

    @Column(name = "login_time", nullable = false)
    @Builder.Default
    private LocalDateTime loginTime = LocalDateTime.now();

    @Column(name = "last_activity")
    @Builder.Default
    private LocalDateTime lastActivity = LocalDateTime.now();

    @Column(name = "logout_time")
    private LocalDateTime logoutTime;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "device_info")
    private String deviceInfo;
}
