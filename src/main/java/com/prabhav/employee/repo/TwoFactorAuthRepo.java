package com.prabhav.employee.repo;

import com.prabhav.employee.entity.TwoFactorAuth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TwoFactorAuthRepo extends JpaRepository<TwoFactorAuth, Long> {
    
    Optional<TwoFactorAuth> findByUserEmail(String userEmail);
    
    void deleteByUserEmail(String userEmail);
}
