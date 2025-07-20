package com.prabhav.employee.service;

import com.prabhav.employee.dto.UserSessionResponse;
import com.prabhav.employee.entity.UserSession;
import com.prabhav.employee.repo.UserSessionRepo;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SessionService {

    private final UserSessionRepo userSessionRepo;

    public String createSession(String userEmail, HttpServletRequest request) {
        String sessionId = UUID.randomUUID().toString();
        
        UserSession session = UserSession.builder()
                .sessionId(sessionId)
                .userEmail(userEmail)
                .ipAddress(getClientIpAddress(request))
                .userAgent(request.getHeader("User-Agent"))
                .deviceInfo(extractDeviceInfo(request.getHeader("User-Agent")))
                .loginTime(LocalDateTime.now())
                .lastActivity(LocalDateTime.now())
                .isActive(true)
                .build();
        
        userSessionRepo.save(session);
        log.info("New session created for user: {} with session ID: {}", userEmail, sessionId);
        
        return sessionId;
    }

    public boolean isSessionValid(String sessionId) {
        Optional<UserSession> session = userSessionRepo.findBySessionIdAndIsActiveTrue(sessionId);
        if (session.isPresent()) {
            updateLastActivity(sessionId);
            return true;
        }
        return false;
    }

    public void updateLastActivity(String sessionId) {
        userSessionRepo.updateLastActivity(sessionId, LocalDateTime.now());
    }

    public void endSession(String sessionId) {
        userSessionRepo.deactivateSession(sessionId, LocalDateTime.now());
        log.info("Session ended: {}", sessionId);
    }

    public void endAllUserSessions(String userEmail) {
        userSessionRepo.deactivateAllUserSessions(userEmail, LocalDateTime.now());
        log.info("All sessions ended for user: {}", userEmail);
    }

    public List<UserSessionResponse> getUserActiveSessions(String userEmail) {
        List<UserSession> sessions = userSessionRepo.findByUserEmailAndIsActiveTrueOrderByLoginTimeDesc(userEmail);
        return sessions.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public List<UserSessionResponse> getUserAllSessions(String userEmail) {
        List<UserSession> sessions = userSessionRepo.findByUserEmailOrderByLoginTimeDesc(userEmail);
        return sessions.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    private UserSessionResponse mapToResponse(UserSession session) {
        return UserSessionResponse.builder()
                .id(session.getId())
                .sessionId(session.getSessionId())
                .userEmail(session.getUserEmail())
                .ipAddress(session.getIpAddress())
                .userAgent(session.getUserAgent())
                .loginTime(session.getLoginTime())
                .lastActivity(session.getLastActivity())
                .logoutTime(session.getLogoutTime())
                .isActive(session.getIsActive())
                .deviceInfo(session.getDeviceInfo())
                .build();
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }

    private String extractDeviceInfo(String userAgent) {
        if (userAgent == null) return "Unknown";
        
        if (userAgent.contains("Mobile") || userAgent.contains("Android")) {
            return "Mobile";
        } else if (userAgent.contains("iPad") || userAgent.contains("Tablet")) {
            return "Tablet";
        } else {
            return "Desktop";
        }
    }
}
