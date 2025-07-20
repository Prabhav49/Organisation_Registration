package com.prabhav.employee.controller;

import com.prabhav.employee.auth.JwtUtil;
import com.prabhav.employee.dto.AdminUserResponse;
import com.prabhav.employee.dto.AuditLogResponse;
import com.prabhav.employee.dto.UserSessionResponse;
import com.prabhav.employee.entity.Role;
import com.prabhav.employee.service.AuditService;
import com.prabhav.employee.service.EmployeeService;
import com.prabhav.employee.service.SessionService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AuditService auditService;
    private final SessionService sessionService;
    private final EmployeeService employeeService;
    private final JwtUtil jwtUtil;

    @GetMapping("/audit-logs")
    public ResponseEntity<List<AuditLogResponse>> getAuditLogs(
            @RequestParam(required = false) String userEmail,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            HttpServletRequest httpRequest) {
        
        String token = getTokenFromRequest(httpRequest);
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(401).body(null);
        }

        // Check admin privileges
        if (!jwtUtil.hasAnyRole(token.substring(7), Role.ADMIN, Role.SUPER_ADMIN)) {
            return ResponseEntity.status(403).body(null);
        }

        List<AuditLogResponse> logs;
        if (userEmail != null) {
            logs = auditService.getUserAuditLogs(userEmail);
        } else if (startDate != null && endDate != null) {
            logs = auditService.getAuditLogsByDateRange(startDate, endDate);
        } else {
            // Return recent audit logs (last 1000 entries)
            logs = auditService.getRecentAuditLogs(1000);
        }

        return ResponseEntity.ok(logs);
    }

    @GetMapping("/audit-logs/{entityType}/{entityId}")
    public ResponseEntity<List<AuditLogResponse>> getEntityAuditLogs(
            @PathVariable String entityType,
            @PathVariable String entityId,
            HttpServletRequest httpRequest) {
        
        String token = getTokenFromRequest(httpRequest);
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(401).body(null);
        }

        if (!jwtUtil.hasAnyRole(token.substring(7), Role.ADMIN, Role.SUPER_ADMIN)) {
            return ResponseEntity.status(403).body(null);
        }

        List<AuditLogResponse> logs = auditService.getEntityAuditLogs(entityType, entityId);
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/sessions/active")
    public ResponseEntity<List<UserSessionResponse>> getActiveSessions(
            @RequestParam(required = false) String userEmail,
            HttpServletRequest httpRequest) {
        
        String token = getTokenFromRequest(httpRequest);
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(401).body(null);
        }

        if (!jwtUtil.hasAnyRole(token.substring(7), Role.ADMIN, Role.SUPER_ADMIN)) {
            return ResponseEntity.status(403).body(null);
        }

        List<UserSessionResponse> sessions;
        if (userEmail != null) {
            sessions = sessionService.getUserActiveSessions(userEmail);
        } else {
            // For admin view, you might want to implement a method to get all active sessions
            // For now, return empty list
            sessions = List.of();
        }

        return ResponseEntity.ok(sessions);
    }

    @PostMapping("/sessions/{sessionId}/terminate")
    public ResponseEntity<Map<String, String>> terminateSession(
            @PathVariable String sessionId,
            HttpServletRequest httpRequest) {
        
        String token = getTokenFromRequest(httpRequest);
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

        if (!jwtUtil.hasAnyRole(token.substring(7), Role.ADMIN, Role.SUPER_ADMIN)) {
            return ResponseEntity.status(403).body(Map.of("error", "Insufficient privileges"));
        }

        sessionService.endSession(sessionId);
        
        String adminEmail = jwtUtil.extractEmail(token.substring(7));
        auditService.logAction(adminEmail, "TERMINATE_SESSION", "UserSession", sessionId, 
                             "Session active", "Session terminated by admin", "SUCCESS");

        return ResponseEntity.ok(Map.of("message", "Session terminated successfully"));
    }

    @PostMapping("/sessions/user/{userEmail}/terminate-all")
    public ResponseEntity<Map<String, String>> terminateAllUserSessions(
            @PathVariable String userEmail,
            HttpServletRequest httpRequest) {
        
        String token = getTokenFromRequest(httpRequest);
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

        if (!jwtUtil.hasAnyRole(token.substring(7), Role.ADMIN, Role.SUPER_ADMIN)) {
            return ResponseEntity.status(403).body(Map.of("error", "Insufficient privileges"));
        }

        sessionService.endAllUserSessions(userEmail);
        
        String adminEmail = jwtUtil.extractEmail(token.substring(7));
        auditService.logAction(adminEmail, "TERMINATE_ALL_SESSIONS", "UserSession", userEmail, 
                             "User sessions active", "All user sessions terminated by admin", "SUCCESS");

        return ResponseEntity.ok(Map.of("message", "All user sessions terminated successfully"));
    }

    @PostMapping("/users/{userId}/2fa/enable")
    public ResponseEntity<Map<String, String>> enableUserTwoFactor(
            @PathVariable Long userId,
            HttpServletRequest httpRequest) {
        
        String token = getTokenFromRequest(httpRequest);
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

        // Only super admin can manage other users' 2FA
        if (!jwtUtil.hasRole(token.substring(7), Role.SUPER_ADMIN)) {
            return ResponseEntity.status(403).body(Map.of("error", "Insufficient privileges"));
        }

        try {
            employeeService.enableUserTwoFactor(userId);
            return ResponseEntity.ok(Map.of("message", "2FA enabled for user"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/users/{userId}/2fa/disable")
    public ResponseEntity<Map<String, String>> disableUserTwoFactor(
            @PathVariable Long userId,
            HttpServletRequest httpRequest) {
        
        String token = getTokenFromRequest(httpRequest);
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

        // Only super admin can manage other users' 2FA
        if (!jwtUtil.hasRole(token.substring(7), Role.SUPER_ADMIN)) {
            return ResponseEntity.status(403).body(Map.of("error", "Insufficient privileges"));
        }

        try {
            employeeService.disableUserTwoFactor(userId);
            return ResponseEntity.ok(Map.of("message", "2FA disabled for user"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/users/{userId}/lock")
    public ResponseEntity<Map<String, String>> lockUser(
            @PathVariable Long userId,
            HttpServletRequest httpRequest) {
        
        String token = getTokenFromRequest(httpRequest);
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

        // Only super admin can lock/unlock users
        if (!jwtUtil.hasRole(token.substring(7), Role.SUPER_ADMIN)) {
            return ResponseEntity.status(403).body(Map.of("error", "Insufficient privileges"));
        }

        try {
            employeeService.lockUser(userId);
            return ResponseEntity.ok(Map.of("message", "User account locked"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/users/{userId}/unlock")
    public ResponseEntity<Map<String, String>> unlockUser(
            @PathVariable Long userId,
            HttpServletRequest httpRequest) {
        
        String token = getTokenFromRequest(httpRequest);
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

        // Only super admin can lock/unlock users
        if (!jwtUtil.hasRole(token.substring(7), Role.SUPER_ADMIN)) {
            return ResponseEntity.status(403).body(Map.of("error", "Insufficient privileges"));
        }

        try {
            employeeService.unlockUser(userId);
            return ResponseEntity.ok(Map.of("message", "User account unlocked"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/users")
    public ResponseEntity<List<AdminUserResponse>> getAllUsers(HttpServletRequest httpRequest) {
        String token = getTokenFromRequest(httpRequest);
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(401).body(null);
        }

        // Only super admin can view all users
        if (!jwtUtil.hasRole(token.substring(7), Role.SUPER_ADMIN)) {
            return ResponseEntity.status(403).body(null);
        }

        try {
            List<AdminUserResponse> users = employeeService.getAllUsersForAdmin();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @PostMapping("/users")
    public ResponseEntity<Map<String, String>> createUser(
            @RequestBody Map<String, String> userRequest,
            HttpServletRequest httpRequest) {
        
        String token = getTokenFromRequest(httpRequest);
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

        // Only super admin can create users
        if (!jwtUtil.hasRole(token.substring(7), Role.SUPER_ADMIN)) {
            return ResponseEntity.status(403).body(Map.of("error", "Insufficient privileges"));
        }

        try {
            // Create user using EmployeeService
            String email = userRequest.get("email");
            String firstName = userRequest.get("firstName");
            String lastName = userRequest.get("lastName");
            String password = userRequest.get("password");
            String roleStr = userRequest.get("role");
            
            if (email == null || firstName == null || password == null || roleStr == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Missing required fields"));
            }
            
            Role role = Role.valueOf(roleStr);
            employeeService.createUserByAdmin(email, firstName, lastName, password, role);
            
            return ResponseEntity.ok(Map.of("message", "User created successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/users/{userId}/role")
    public ResponseEntity<Map<String, String>> updateUserRole(
            @PathVariable Long userId,
            @RequestBody Map<String, String> roleRequest,
            HttpServletRequest httpRequest) {
        
        String token = getTokenFromRequest(httpRequest);
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

        // Only super admin can update roles
        if (!jwtUtil.hasRole(token.substring(7), Role.SUPER_ADMIN)) {
            return ResponseEntity.status(403).body(Map.of("error", "Insufficient privileges"));
        }

        try {
            String roleStr = roleRequest.get("role");
            if (roleStr == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Role is required"));
            }
            
            Role newRole = Role.valueOf(roleStr);
            employeeService.updateUserRole(userId, newRole);
            
            return ResponseEntity.ok(Map.of("message", "Role updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Map<String, String>> deleteUser(
            @PathVariable Long userId,
            HttpServletRequest httpRequest) {
        
        String token = getTokenFromRequest(httpRequest);
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

        // Only super admin can delete users
        if (!jwtUtil.hasRole(token.substring(7), Role.SUPER_ADMIN)) {
            return ResponseEntity.status(403).body(Map.of("error", "Insufficient privileges"));
        }

        try {
            employeeService.deleteEmployee(userId);
            return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to delete user"));
        }
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        return request.getHeader("Authorization");
    }
}
