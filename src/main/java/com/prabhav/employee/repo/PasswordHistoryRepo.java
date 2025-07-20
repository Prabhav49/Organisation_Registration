package com.prabhav.employee.repo;

import com.prabhav.employee.entity.PasswordHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PasswordHistoryRepo extends JpaRepository<PasswordHistory, Long> {
    
    @Query("SELECT p FROM PasswordHistory p WHERE p.userEmail = :userEmail ORDER BY p.createdAt DESC")
    List<PasswordHistory> findByUserEmailOrderByCreatedAtDesc(@Param("userEmail") String userEmail);
    
    @Query("SELECT p FROM PasswordHistory p WHERE p.userEmail = :userEmail ORDER BY p.createdAt DESC LIMIT :limit")
    List<PasswordHistory> findTopNByUserEmailOrderByCreatedAtDesc(@Param("userEmail") String userEmail, @Param("limit") int limit);
}
