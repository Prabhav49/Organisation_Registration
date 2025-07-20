package com.prabhav.employee.repo;

import com.prabhav.employee.entity.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserSessionRepo extends JpaRepository<UserSession, Long> {
    
    Optional<UserSession> findBySessionIdAndIsActiveTrue(String sessionId);
    
    List<UserSession> findByUserEmailAndIsActiveTrueOrderByLoginTimeDesc(String userEmail);
    
    List<UserSession> findByUserEmailOrderByLoginTimeDesc(String userEmail);
    
    @Modifying
    @Transactional
    @Query("UPDATE UserSession s SET s.isActive = false, s.logoutTime = :logoutTime WHERE s.sessionId = :sessionId")
    void deactivateSession(@Param("sessionId") String sessionId, @Param("logoutTime") LocalDateTime logoutTime);
    
    @Modifying
    @Transactional
    @Query("UPDATE UserSession s SET s.isActive = false, s.logoutTime = :logoutTime WHERE s.userEmail = :userEmail AND s.isActive = true")
    void deactivateAllUserSessions(@Param("userEmail") String userEmail, @Param("logoutTime") LocalDateTime logoutTime);
    
    @Modifying
    @Transactional
    @Query("UPDATE UserSession s SET s.lastActivity = :lastActivity WHERE s.sessionId = :sessionId")
    void updateLastActivity(@Param("sessionId") String sessionId, @Param("lastActivity") LocalDateTime lastActivity);
}
