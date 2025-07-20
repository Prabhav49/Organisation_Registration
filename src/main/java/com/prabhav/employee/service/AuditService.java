package com.prabhav.employee.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prabhav.employee.dto.AuditLogResponse;
import com.prabhav.employee.entity.AuditLog;
import com.prabhav.employee.repo.AuditLogRepo;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditService {

    private final AuditLogRepo auditLogRepo;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void logAction(String userEmail, String action, String entityType, String entityId, 
                         Object oldValues, Object newValues, String status) {
        try {
            HttpServletRequest request = getCurrentRequest();
            
            AuditLog auditLog = AuditLog.builder()
                    .userEmail(userEmail)
                    .action(action)
                    .entityType(entityType)
                    .entityId(entityId)
                    .oldValues(oldValues != null ? objectMapper.writeValueAsString(oldValues) : null)
                    .newValues(newValues != null ? objectMapper.writeValueAsString(newValues) : null)
                    .ipAddress(request != null ? getClientIpAddress(request) : "unknown")
                    .userAgent(request != null ? request.getHeader("User-Agent") : "unknown")
                    .timestamp(LocalDateTime.now())
                    .status(status)
                    .build();
            
            auditLogRepo.save(auditLog);
            log.info("Audit log created for user: {}, action: {}", userEmail, action);
        } catch (JsonProcessingException e) {
            log.error("Error serializing audit log data", e);
        } catch (Exception e) {
            log.error("Error creating audit log", e);
        }
    }

    public void logFailedAction(String userEmail, String action, String entityType, String entityId, String errorMessage) {
        try {
            HttpServletRequest request = getCurrentRequest();
            
            AuditLog auditLog = AuditLog.builder()
                    .userEmail(userEmail)
                    .action(action)
                    .entityType(entityType)
                    .entityId(entityId)
                    .ipAddress(request != null ? getClientIpAddress(request) : "unknown")
                    .userAgent(request != null ? request.getHeader("User-Agent") : "unknown")
                    .timestamp(LocalDateTime.now())
                    .status("FAILED")
                    .errorMessage(errorMessage)
                    .build();
            
            auditLogRepo.save(auditLog);
        } catch (Exception e) {
            log.error("Error creating failed audit log", e);
        }
    }

    public List<AuditLogResponse> getUserAuditLogs(String userEmail) {
        List<AuditLog> logs = auditLogRepo.findByUserEmailOrderByTimestampDesc(userEmail);
        return logs.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public List<AuditLogResponse> getEntityAuditLogs(String entityType, String entityId) {
        List<AuditLog> logs = auditLogRepo.findByEntityTypeAndEntityIdOrderByTimestampDesc(entityType, entityId);
        return logs.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public List<AuditLogResponse> getAuditLogsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        List<AuditLog> logs = auditLogRepo.findByTimestampBetween(startDate, endDate);
        return logs.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public List<AuditLogResponse> getRecentAuditLogs(int limit) {
        List<AuditLog> logs = auditLogRepo.findAllByOrderByTimestampDesc();
        return logs.stream()
                .limit(limit)
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private AuditLogResponse mapToResponse(AuditLog auditLog) {
        return AuditLogResponse.builder()
                .id(auditLog.getId())
                .userEmail(auditLog.getUserEmail())
                .action(auditLog.getAction())
                .entityType(auditLog.getEntityType())
                .entityId(auditLog.getEntityId())
                .oldValues(auditLog.getOldValues())
                .newValues(auditLog.getNewValues())
                .ipAddress(auditLog.getIpAddress())
                .userAgent(auditLog.getUserAgent())
                .timestamp(auditLog.getTimestamp())
                .status(auditLog.getStatus())
                .errorMessage(auditLog.getErrorMessage())
                .build();
    }

    private HttpServletRequest getCurrentRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
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
}
