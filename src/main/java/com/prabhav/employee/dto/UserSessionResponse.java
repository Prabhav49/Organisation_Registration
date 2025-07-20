package com.prabhav.employee.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSessionResponse {
    
    private Long id;
    private String sessionId;
    private String userEmail;
    private String ipAddress;
    private String userAgent;
    private LocalDateTime loginTime;
    private LocalDateTime lastActivity;
    private LocalDateTime logoutTime;
    private Boolean isActive;
    private String deviceInfo;
}
